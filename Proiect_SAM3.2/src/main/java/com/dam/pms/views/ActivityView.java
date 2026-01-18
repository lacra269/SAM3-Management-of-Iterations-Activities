package com.dam.pms.views;

import com.dam.pms.config.ServiceLocator;
import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.Iteration;
import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.entity.Subtask;
import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.enums.ActivityStatus;
import com.dam.pms.domain.enums.Priority;
import com.dam.pms.domain.service.workflow.WorkflowRulesEngine;
import com.dam.pms.infrastructure.repository.IterationRepository;
import com.dam.pms.infrastructure.repository.ProjectRepository;
import com.dam.pms.ui.service.ActivityUIService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@Route("activities")
@PageTitle("Activități")
public class ActivityView extends VerticalLayout {

    private final ActivityUIService activityUIService;
    private final ProjectRepository projectRepository;
    private final IterationRepository iterationRepository;
    private final WorkflowRulesEngine workflowRulesEngine;

    private final Grid<Activity> activityGrid = new Grid<>(Activity.class, false);
    private final ComboBox<Project> projectCombo = new ComboBox<>("Selectează Proiect");
    private final Button addBtn = new Button("➕ Adaugă activitate");

    private Project selectedProject;

    @Autowired
    public ActivityView(
            ActivityUIService activityUIService,
            ProjectRepository projectRepository,
            IterationRepository iterationRepository,
            WorkflowRulesEngine workflowRulesEngine) {
        this.activityUIService = activityUIService;
        this.projectRepository = projectRepository;
        this.iterationRepository = iterationRepository;
        this.workflowRulesEngine = workflowRulesEngine;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1("📌 Activități");

        setupProjectSelector();

        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(e -> openActivityDialog(null));
        addBtn.setEnabled(false);

        Button refreshBtn = new Button("🔄 Refresh");
        refreshBtn.addClickListener(e -> loadActivities());

        HorizontalLayout toolbar = new HorizontalLayout(addBtn, refreshBtn);
        toolbar.setSpacing(true);

        configureGrid();

        add(title, projectCombo, toolbar, activityGrid);
    }

    private void setupProjectSelector() {
        projectCombo.setWidthFull();
        projectCombo.setItemLabelGenerator(Project::getName);
        projectCombo.setPlaceholder("Selectează un proiect...");

        projectCombo.addValueChangeListener(e -> {
            selectedProject = e.getValue();
            if (selectedProject != null) {
                addBtn.setEnabled(true);
                loadActivities();
            } else {
                activityGrid.setItems();
                addBtn.setEnabled(false);
            }
        });

        List<Project> projects = projectRepository.findAll();
        projectCombo.setItems(projects);
    }

    private void configureGrid() {
        activityGrid.removeAllColumns();

        activityGrid.addColumn(Activity::getTitle)
                .setHeader("Titlu")
                .setSortable(true)
                .setAutoWidth(true);

        activityGrid.addColumn(a -> a.getIteration() != null ? a.getIteration().getName() : "-")
                .setHeader("Iterație")
                .setAutoWidth(true);

        // ✅ COLOANĂ pentru membru asignat
        activityGrid.addColumn(a -> a.getAssignedTo() != null ? a.getAssignedTo().getName() : "Neasignat")
                .setHeader("Asignat către")
                .setAutoWidth(true);

        activityGrid.addComponentColumn(activity -> {
            VerticalLayout layout = new VerticalLayout();
            layout.setPadding(false);
            layout.setSpacing(false);

            double progress = calculateProgress(activity);
            ProgressBar progressBar = new ProgressBar(0, 100, progress);
            progressBar.setWidth("100px");

            String color = progress == 100 ? "success" : progress >= 50 ? "contrast" : "error";
            progressBar.getElement().getThemeList().add(color);

            Span label = new Span((int) progress + "%");
            label.getStyle().set("font-size", "12px");

            layout.add(progressBar, label);
            return layout;
        }).setHeader("Progres").setAutoWidth(true);

        activityGrid.addColumn(activity -> {
            int total = activity.getSubtasks() != null ? activity.getSubtasks().size() : 0;
            int done = activity.getSubtasks() != null ?
                    (int) activity.getSubtasks().stream()
                            .filter(s -> s.getStatus() == ActivityStatus.DONE)
                            .count() : 0;
            return done + "/" + total;
        }).setHeader("Subtasks").setAutoWidth(true);

        activityGrid.addComponentColumn(activity -> {
            Span badge = new Span(activity.getStatus() != null ? activity.getStatus().name() : "TODO");
            badge.getElement().getThemeList().add("badge");

            String theme = switch (activity.getStatus() != null ? activity.getStatus() : ActivityStatus.TODO) {
                case DONE -> "success";
                case IN_PROGRESS -> "contrast";
                case BLOCKED -> "error";
                case IN_REVIEW -> "primary";
                default -> "";
            };

            if (!theme.isEmpty()) {
                badge.getElement().getThemeList().add(theme);
            }

            return badge;
        }).setHeader("Status").setSortable(true);

        activityGrid.addColumn(activity ->
                        activity.getPriority() != null ? activity.getPriority().name() : "")
                .setHeader("Prioritate")
                .setSortable(true);

        activityGrid.addComponentColumn(activity -> {
            Button editBtn = new Button("✏️");
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editBtn.addClickListener(e -> openActivityDialog(activity));

            Button subtasksBtn = new Button("📋 Subtasks");
            subtasksBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
            subtasksBtn.addClickListener(e -> openSubtasksDialog(activity));

            Button deleteBtn = new Button("🗑️");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> deleteActivity(activity));

            return new HorizontalLayout(editBtn, subtasksBtn, deleteBtn);
        }).setHeader("Acțiuni").setAutoWidth(true);

        activityGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        activityGrid.setSizeFull();
    }

    private double calculateProgress(Activity activity) {
        if (activity.getSubtasks() == null || activity.getSubtasks().isEmpty()) {
            return switch (activity.getStatus() != null ? activity.getStatus() : ActivityStatus.TODO) {
                case DONE -> 100.0;
                case IN_REVIEW -> 75.0;
                case IN_PROGRESS -> 50.0;
                case TODO -> 0.0;
                default -> 25.0;
            };
        }

        long total = activity.getSubtasks().size();
        long done = activity.getSubtasks().stream()
                .filter(s -> s.getStatus() == ActivityStatus.DONE)
                .count();

        return (done * 100.0) / total;
    }

    private void loadActivities() {
        if (selectedProject == null) {
            activityGrid.setItems();
            return;
        }

        try {
            List<Activity> activities = activityUIService.findByProjectId(selectedProject.getId());
            activityGrid.setItems(activities);

            Notification.show(activities.size() + " activități încărcate",
                            2000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Eroare: " + e.getMessage(),
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void openSubtasksDialog(Activity activity) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeaderTitle("📋 Subtasks - " + activity.getTitle());

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(true);

        double progress = calculateProgress(activity);
        ProgressBar progressBar = new ProgressBar(0, 100, progress);
        progressBar.setWidth("100%");

        Span progressLabel = new Span("Progres: " + (int) progress + "%");
        progressLabel.getStyle().set("font-weight", "bold");

        Span statusLabel = new Span("Status activitate: " +
                (activity.getStatus() != null ? activity.getStatus().name() : "TODO"));

        layout.add(progressLabel, progressBar, statusLabel);

        Grid<Subtask> subtaskGrid = new Grid<>(Subtask.class, false);
        subtaskGrid.setHeight("300px");

        subtaskGrid.addColumn(Subtask::getName).setHeader("Nume").setAutoWidth(true);

        subtaskGrid.addComponentColumn(subtask -> {
            ComboBox<ActivityStatus> statusCombo = new ComboBox<>();
            statusCombo.setItems(ActivityStatus.values());
            statusCombo.setValue(subtask.getStatus() != null ? subtask.getStatus() : ActivityStatus.TODO);
            statusCombo.setWidth("150px");

            statusCombo.addValueChangeListener(e -> {
                if (e.getValue() != null) {
                    updateSubtaskStatus(activity, subtask, e.getValue(), dialog);
                }
            });

            return statusCombo;
        }).setHeader("Status").setAutoWidth(true);

        subtaskGrid.addComponentColumn(subtask -> {
            Button deleteBtn = new Button("🗑️");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> {
                activity.getSubtasks().remove(subtask);
                activityUIService.save(activity);
                updateActivityStatusBasedOnSubtasks(activity);
                dialog.close();
                openSubtasksDialog(activity);
                loadActivities();
            });
            return deleteBtn;
        }).setHeader("").setAutoWidth(true);

        subtaskGrid.setItems(activity.getSubtasks());
        layout.add(subtaskGrid);

        HorizontalLayout addLayout = new HorizontalLayout();
        TextField nameField = new TextField("Nume subtask");
        nameField.setPlaceholder("ex: Implementare API");

        Button addSubBtn = new Button("➕ Adaugă");
        addSubBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addSubBtn.addClickListener(e -> {
            if (!nameField.isEmpty()) {
                Subtask newSubtask = new Subtask();
                newSubtask.setName(nameField.getValue());
                newSubtask.setStatus(ActivityStatus.TODO);
                activity.addSubtask(newSubtask);

                activityUIService.save(activity);
                updateActivityStatusBasedOnSubtasks(activity);

                dialog.close();
                openSubtasksDialog(activity);
                loadActivities();

                Notification.show("✅ Subtask adăugat!", 2000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        });

        addLayout.add(nameField, addSubBtn);
        addLayout.setAlignItems(Alignment.END);
        layout.add(addLayout);

        Button closeBtn = new Button("Închide", e -> dialog.close());
        layout.add(closeBtn);

        dialog.add(layout);
        dialog.open();
    }

    private void updateSubtaskStatus(Activity activity, Subtask subtask, ActivityStatus newStatus, Dialog dialog) {
        subtask.setStatus(newStatus);
        activityUIService.save(activity);

        ActivityStatus derivedStatus = workflowRulesEngine.deriveActivityStatusFromSubtasks(activity);

        if (derivedStatus != activity.getStatus()) {
            activity.setStatus(derivedStatus);
            activityUIService.save(activity);

            Notification.show("✨ Status activitate actualizat: " + derivedStatus.name(),
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }

        dialog.close();
        openSubtasksDialog(activity);
        loadActivities();
    }

    private void updateActivityStatusBasedOnSubtasks(Activity activity) {
        ActivityStatus derivedStatus = workflowRulesEngine.deriveActivityStatusFromSubtasks(activity);

        if (derivedStatus != activity.getStatus()) {
            activity.setStatus(derivedStatus);
            activityUIService.save(activity);

            Notification.show("✨ Status actualizat: " + derivedStatus.name(),
                            2000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
    }

    private void openActivityDialog(Activity activity) {
        boolean editMode = activity != null;
        final Activity currentActivity = editMode ? activity : new Activity();

        Dialog dialog = new Dialog();
        dialog.setWidth("550px");
        dialog.setHeaderTitle(editMode ? "✏️ Editează activitate" : "➕ Adaugă activitate");

        TextField titleField = new TextField("Titlu");
        titleField.setWidthFull();
        titleField.setRequired(true);

        TextArea descriptionField = new TextArea("Descriere");
        descriptionField.setWidthFull();

        // ✅ ComboBox pentru iterație
        ComboBox<Iteration> iterationCombo = new ComboBox<>("Iterație");
        List<Iteration> iterations = iterationRepository.findByProject(selectedProject);
        iterationCombo.setItems(iterations);
        iterationCombo.setItemLabelGenerator(it -> it.getName() + " (Sprint " + it.getNumber() + ")");
        iterationCombo.setWidthFull();
        iterationCombo.setRequired(true);

        // ✅ ComboBox pentru asignare membru
        ComboBox<TeamMember> memberCombo = new ComboBox<>("Asignează Membru");
        try {
            List<TeamMember> members = ServiceLocator.getTeamMemberUIService().findAll();
            memberCombo.setItems(members);
            memberCombo.setItemLabelGenerator(TeamMember::getName);
            memberCombo.setWidthFull();
            memberCombo.setClearButtonVisible(true);
            memberCombo.setPlaceholder("Selectează membru (opțional)...");
        } catch (Exception ex) {
            Notification.show("⚠️ Nu s-au putut încărca membrii",
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
        }

        ComboBox<ActivityStatus> statusCombo = new ComboBox<>("Status");
        statusCombo.setItems(ActivityStatus.values());
        statusCombo.setWidthFull();

        ComboBox<Priority> priorityCombo = new ComboBox<>("Prioritate");
        priorityCombo.setItems(Priority.values());
        priorityCombo.setWidthFull();

        NumberField estimatedField = new NumberField("Ore estimate");
        estimatedField.setWidthFull();
        estimatedField.setMin(0);

        DatePicker createdDatePicker = new DatePicker("Data creării");
        createdDatePicker.setWidthFull();

        DatePicker dueDatePicker = new DatePicker("Data scadentă");
        dueDatePicker.setWidthFull();

        if (editMode) {
            titleField.setValue(currentActivity.getTitle() != null ? currentActivity.getTitle() : "");
            descriptionField.setValue(currentActivity.getDescription() != null ? currentActivity.getDescription() : "");
            iterationCombo.setValue(currentActivity.getIteration());
            memberCombo.setValue(currentActivity.getAssignedTo()); // ✅ Setează membrul existent
            statusCombo.setValue(currentActivity.getStatus() != null ? currentActivity.getStatus() : ActivityStatus.TODO);
            priorityCombo.setValue(currentActivity.getPriority() != null ? currentActivity.getPriority() : Priority.MEDIUM);

            if (currentActivity.getEstimatedHours() != null)
                estimatedField.setValue(currentActivity.getEstimatedHours());
            if (currentActivity.getCreatedDate() != null)
                createdDatePicker.setValue(currentActivity.getCreatedDate());
            if (currentActivity.getDueDate() != null)
                dueDatePicker.setValue(currentActivity.getDueDate());
        } else {
            statusCombo.setValue(ActivityStatus.TODO);
            priorityCombo.setValue(Priority.MEDIUM);
            createdDatePicker.setValue(LocalDate.now());
            dueDatePicker.setValue(LocalDate.now().plusDays(7));

            if (!iterations.isEmpty()) {
                iterationCombo.setValue(iterations.get(0));
            }
        }

        Button saveBtn = new Button("💾 Salvează", e -> {
            if (titleField.isEmpty()) {
                Notification.show("⚠️ Titlul este obligatoriu!", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            currentActivity.setTitle(titleField.getValue());
            currentActivity.setDescription(descriptionField.getValue());
            currentActivity.setIteration(iterationCombo.getValue());
            currentActivity.setAssignedTo(memberCombo.getValue()); // ✅ Setează membrul asignat
            currentActivity.setStatus(statusCombo.getValue());
            currentActivity.setPriority(priorityCombo.getValue());
            currentActivity.setEstimatedHours(estimatedField.getValue());
            currentActivity.setCreatedDate(createdDatePicker.getValue());
            currentActivity.setDueDate(dueDatePicker.getValue());

            try {
                activityUIService.save(currentActivity);
                dialog.close();
                loadActivities();

                String message = "✅ Salvat!";
                if (memberCombo.getValue() != null) {
                    message += " Asignat: " + memberCombo.getValue().getName();
                }

                Notification.show(message, 2000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show("❌ Eroare: " + ex.getMessage(), 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Anulează", e -> dialog.close());

        HorizontalLayout buttonLayout = new HorizontalLayout(saveBtn, cancelBtn);

        VerticalLayout formLayout = new VerticalLayout(
                titleField, descriptionField, iterationCombo, memberCombo,
                statusCombo, priorityCombo,
                estimatedField, createdDatePicker, dueDatePicker, buttonLayout
        );
        formLayout.setSpacing(true);
        formLayout.setPadding(true);

        dialog.add(formLayout);
        dialog.open();
    }

    private void deleteActivity(Activity activity) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("🗑️ Confirmare");

        VerticalLayout content = new VerticalLayout();
        content.add("Ștergi: " + activity.getTitle() + "?");

        Button confirmBtn = new Button("Da", e -> {
            activityUIService.delete(activity);
            confirmDialog.close();
            loadActivities();

            Notification.show("✅ Șters!", 2000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Nu", e -> confirmDialog.close());

        content.add(new HorizontalLayout(confirmBtn, cancelBtn));
        confirmDialog.add(content);
        confirmDialog.open();
    }
}

