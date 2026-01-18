package com.dam.pms.views;

import com.dam.pms.config.ServiceLocator;
import com.dam.pms.domain.entity.Activity;
import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.entity.Subtask;
import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.enums.ActivityStatus;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("subtasks")
@PageTitle("Subtasks Dashboard")
public class SubtaskView extends VerticalLayout {

    private final ComboBox<Project> projectCombo = new ComboBox<>("Selectează Proiect");
    private final Grid<Activity> activityGrid = new Grid<>(Activity.class, false);
    private final Grid<Subtask> subtaskGrid = new Grid<>(Subtask.class, false);

    private Long selectedActivityId;
    private Button addSubtaskBtn;

    public SubtaskView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H1 title = new H1(" Subtasks Dashboard");

        addSubtaskBtn = new Button(" Adaugă Subtask");
        addSubtaskBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addSubtaskBtn.setEnabled(false);
        addSubtaskBtn.addClickListener(e -> openAddSubtaskDialog());

        Button refreshBtn = new Button(" Refresh");
        refreshBtn.addClickListener(e -> refreshData());

        HorizontalLayout toolbar = new HorizontalLayout(addSubtaskBtn, refreshBtn);
        toolbar.setSpacing(true);

        setupProjectCombo();
        setupActivityGrid();
        setupSubtaskGrid();

        add(title, projectCombo,
                new H3(" Activități"),
                activityGrid,
                toolbar,
                new H3(" Subtasks"),
                subtaskGrid);
    }

    /* ================= PROJECT COMBO ================= */
    private void setupProjectCombo() {
        projectCombo.setWidthFull();

        try {
            List<Project> projects = ServiceLocator.getProjectUIService().findAll();
            projectCombo.setItems(projects);
            projectCombo.setItemLabelGenerator(Project::getName);
            projectCombo.setPlaceholder("Alege un proiect...");

            projectCombo.addValueChangeListener(event -> {
                Project selected = event.getValue();
                if (selected != null) {
                    loadActivitiesForProject(selected);
                    subtaskGrid.setItems();
                    selectedActivityId = null;
                    addSubtaskBtn.setEnabled(false);
                } else {
                    activityGrid.setItems();
                    subtaskGrid.setItems();
                    selectedActivityId = null;
                    addSubtaskBtn.setEnabled(false);
                }
            });
        } catch (Exception e) {
            Notification.show("Eroare: " + e.getMessage(), 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /* ================= ACTIVITY GRID ================= */
    private void setupActivityGrid() {
        activityGrid.removeAllColumns();

        activityGrid.addColumn(Activity::getTitle)
                .setHeader("Activitate")
                .setSortable(true)
                .setAutoWidth(true);

        activityGrid.addColumn(a -> a.getAssignedTo() != null ? a.getAssignedTo().getName() : "Neasignat")
                .setHeader("Asignat către")
                .setAutoWidth(true);

        activityGrid.addColumn(a -> a.getStatus() != null ? a.getStatus().name() : "")
                .setHeader("Status")
                .setSortable(true)
                .setAutoWidth(true);

        activityGrid.addComponentColumn(a -> {
            int total = a.getSubtasks() != null ? a.getSubtasks().size() : 0;
            int done = a.getSubtasks() != null ?
                    (int) a.getSubtasks().stream()
                            .filter(s -> s.getStatus() == ActivityStatus.DONE)
                            .count() : 0;

            Span badge = new Span(done + "/" + total);
            badge.getElement().getThemeList().add("badge");

            if (total > 0 && done == total) {
                badge.getElement().getThemeList().add("success");
            } else if (done > 0) {
                badge.getElement().getThemeList().add("contrast");
            }

            return badge;
        }).setHeader("Subtasks").setAutoWidth(true);

        activityGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        activityGrid.setHeight("300px");

        activityGrid.asSingleSelect().addValueChangeListener(event -> {
            Activity selected = event.getValue();

            if (selected != null) {
                selectedActivityId = selected.getId();

                //  Afișează subtasks chiar dacă lista e goală
                if (selected.getSubtasks() != null && !selected.getSubtasks().isEmpty()) {
                    subtaskGrid.setItems(selected.getSubtasks());
                    Notification.show(
                            selected.getSubtasks().size() + " subtasks pentru: " + selected.getTitle(),
                            2000, Notification.Position.BOTTOM_END
                    ).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    subtaskGrid.setItems(); // Lista goală
                    Notification.show(
                            "0 subtasks pentru: " + selected.getTitle(),
                            2000, Notification.Position.BOTTOM_END
                    ).addThemeVariants(NotificationVariant.LUMO_CONTRAST);
                }

                addSubtaskBtn.setEnabled(true); //  Activează butonul chiar dacă nu are subtasks
            } else {
                selectedActivityId = null;
                subtaskGrid.setItems();
                addSubtaskBtn.setEnabled(false);
            }
        });
    }

    /* ================= SUBTASK GRID ================= */
    private void setupSubtaskGrid() {
        subtaskGrid.removeAllColumns();

        subtaskGrid.addColumn(Subtask::getName)
                .setHeader("Subtask")
                .setSortable(true)
                .setAutoWidth(true);

        //  COLOANĂ pentru membru asignat DIRECT la subtask
        subtaskGrid.addColumn(s -> s.getAssignedTo() != null ? s.getAssignedTo().getName() : "Neasignat")
                .setHeader("Membru asignat")
                .setAutoWidth(true);

        subtaskGrid.addColumn(s -> s.getStatus() != null ? s.getStatus().name() : "")
                .setHeader("Status")
                .setSortable(true)
                .setAutoWidth(true);

        subtaskGrid.addComponentColumn(subtask -> {
            ComboBox<ActivityStatus> statusCombo = new ComboBox<>();
            statusCombo.setItems(ActivityStatus.values());
            statusCombo.setItemLabelGenerator(ActivityStatus::name);
            statusCombo.setValue(subtask.getStatus() != null ? subtask.getStatus() : ActivityStatus.TODO);
            statusCombo.setWidth("150px");

            statusCombo.addValueChangeListener(e -> {
                if (e.getValue() != null && !e.getValue().equals(e.getOldValue())) {
                    updateSubtaskStatus(subtask, e.getValue());
                }
            });

            return statusCombo;
        }).setHeader("Schimbă Status").setAutoWidth(true);

        //  COLOANĂ pentru editare membru
        subtaskGrid.addComponentColumn(subtask -> {
            Button editMemberBtn = new Button("👤");
            editMemberBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
            editMemberBtn.getElement().setAttribute("title", "Editează membru");
            editMemberBtn.addClickListener(e -> openEditMemberDialog(subtask));
            return editMemberBtn;
        }).setHeader("Membru").setAutoWidth(true);

        subtaskGrid.addComponentColumn(subtask -> {
            Button deleteBtn = new Button("🗑️");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> confirmDeleteSubtask(subtask));
            return deleteBtn;
        }).setHeader("Acțiuni").setAutoWidth(true);

        subtaskGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        subtaskGrid.setHeight("400px");
    }

    /* ================= ADD SUBTASK DIALOG ================= */
    private void openAddSubtaskDialog() {
        if (selectedActivityId == null) {
            Notification.show(" Selectează o activitate mai întâi!",
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }

        Activity activity = ServiceLocator.getActivityUIService()
                .findById(selectedActivityId)
                .orElse(null);

        if (activity == null) {
            Notification.show(" Activitatea nu mai există!",
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setWidth("550px");
        dialog.setHeaderTitle(" Adaugă Subtask - " + activity.getTitle());

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        TextField nameField = new TextField("Nume Subtask");
        nameField.setWidthFull();
        nameField.setPlaceholder("ex: Implementare API endpoint");
        nameField.setRequired(true);
        nameField.focus();

        ComboBox<ActivityStatus> statusCombo = new ComboBox<>("Status Inițial");
        statusCombo.setItems(ActivityStatus.values());
        statusCombo.setItemLabelGenerator(ActivityStatus::name);
        statusCombo.setValue(ActivityStatus.TODO);
        statusCombo.setWidthFull();

        //  ComboBox pentru asignare membru DIRECT la subtask
        ComboBox<TeamMember> memberCombo = new ComboBox<>("Asignează Membru");
        try {
            List<TeamMember> members = ServiceLocator.getTeamMemberUIService().findAll();
            memberCombo.setItems(members);
            memberCombo.setItemLabelGenerator(TeamMember::getName);
            memberCombo.setWidthFull();
            memberCombo.setPlaceholder("Selectează membru (opțional)...");
        } catch (Exception ex) {
            Notification.show(" Nu s-au putut încărca membrii: " + ex.getMessage(),
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
        }

        Span infoText = new Span(" Poți asigna un membru specific acestui subtask");
        infoText.getStyle()
                .set("font-size", "var(--lumo-font-size-s)")
                .set("color", "var(--lumo-secondary-text-color)");

        Button saveBtn = new Button(" Salvează", e -> {
            if (nameField.isEmpty()) {
                Notification.show(" Numele este obligatoriu!",
                                3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            try {
                Subtask newSubtask = new Subtask();
                newSubtask.setName(nameField.getValue());
                newSubtask.setStatus(statusCombo.getValue());
                newSubtask.setActivity(activity);
                newSubtask.setAssignedTo(memberCombo.getValue()); //  Asignare membru

                ServiceLocator.getSubtaskUIService().save(newSubtask);

                dialog.close();
                refreshData();

                String message = "Subtask adăugat cu succes!";
                if (memberCombo.getValue() != null) {
                    message += " Asignat: " + memberCombo.getValue().getName();
                }

                Notification.show(message, 3000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show(" Eroare: " + ex.getMessage(),
                                3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        nameField.addKeyPressListener(event -> {
            if (event.getKey().equals("Enter") && !nameField.isEmpty()) {
                saveBtn.click();
            }
        });

        Button cancelBtn = new Button("Anulează", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);
        buttons.setSpacing(true);

        layout.add(nameField, memberCombo, infoText, statusCombo, buttons);
        dialog.add(layout);
        dialog.open();
    }

    /* ================= EDIT MEMBER DIALOG ================= */
    private void openEditMemberDialog(Subtask subtask) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        dialog.setHeaderTitle(" Editează membru - " + subtask.getName());

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);

        ComboBox<TeamMember> memberCombo = new ComboBox<>("Asignează Membru");
        try {
            List<TeamMember> members = ServiceLocator.getTeamMemberUIService().findAll();
            memberCombo.setItems(members);
            memberCombo.setItemLabelGenerator(TeamMember::getName);
            memberCombo.setWidthFull();
            memberCombo.setClearButtonVisible(true);
            memberCombo.setPlaceholder("Selectează membru...");

            if (subtask.getAssignedTo() != null) {
                memberCombo.setValue(subtask.getAssignedTo());
            }
        } catch (Exception ex) {
            Notification.show(" Nu s-au putut încărca membrii",
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_WARNING);
        }

        Button saveBtn = new Button(" Salvează", e -> {
            try {
                subtask.setAssignedTo(memberCombo.getValue());
                ServiceLocator.getSubtaskUIService().save(subtask);

                dialog.close();
                refreshData();

                String message = memberCombo.getValue() != null
                        ? " Membru actualizat: " + memberCombo.getValue().getName()
                        : " Membru șters";

                Notification.show(message, 2000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show(" Eroare: " + ex.getMessage(),
                                3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Anulează", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveBtn, cancelBtn);
        buttons.setSpacing(true);

        layout.add(memberCombo, buttons);
        dialog.add(layout);
        dialog.open();
    }

    /* ================= LOAD ACTIVITIES ================= */
    private void loadActivitiesForProject(Project project) {
        try {
            List<Activity> activities = ServiceLocator.getActivityUIService()
                    .findByProjectId(project.getId());
            activityGrid.setItems(activities);

            Notification.show(
                    activities.size() + " activități găsite",
                    2000, Notification.Position.BOTTOM_END
            ).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Eroare: " + e.getMessage(),
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            activityGrid.setItems();
        }
    }

    /* ================= UPDATE SUBTASK STATUS ================= */
    private void updateSubtaskStatus(Subtask subtask, ActivityStatus newStatus) {
        try {
            subtask.setStatus(newStatus);
            ServiceLocator.getSubtaskUIService().save(subtask);

            Notification.show(" Status actualizat!",
                            2000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            refreshData();
        } catch (Exception e) {
            Notification.show(" Eroare: " + e.getMessage(),
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /* ================= DELETE SUBTASK ================= */
    private void confirmDeleteSubtask(Subtask subtask) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle(" Confirmare Ștergere");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.add(new Span("Sigur ștergi subtask-ul: " + subtask.getName() + "?"));

        Button confirmBtn = new Button("Da, șterge", e -> {
            try {
                ServiceLocator.getSubtaskUIService().delete(subtask);
                confirmDialog.close();
                refreshData();

                Notification.show(" Subtask șters!",
                                2000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } catch (Exception ex) {
                Notification.show(" Eroare: " + ex.getMessage(),
                                3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        confirmBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Anulează", e -> confirmDialog.close());

        HorizontalLayout buttons = new HorizontalLayout(confirmBtn, cancelBtn);
        content.add(buttons);

        confirmDialog.add(content);
        confirmDialog.open();
    }

    /* ================= REFRESH DATA ================= */
    private void refreshData() {
        if (projectCombo.getValue() == null || selectedActivityId == null) {
            return;
        }

        try {

            List<Activity> activities = ServiceLocator.getActivityUIService()
                    .findByProjectId(projectCombo.getValue().getId());
            activityGrid.setItems(activities);


            activities.stream()
                    .filter(a -> a.getId().equals(selectedActivityId))
                    .findFirst()
                    .ifPresent(a -> {
                        // Re-încarcă explicit activitatea cu subtasks actualizate
                        Activity freshActivity = ServiceLocator.getActivityUIService()
                                .findById(a.getId())
                                .orElse(a);

                        activityGrid.select(freshActivity);
                        subtaskGrid.setItems(freshActivity.getSubtasks());
                    });

        } catch (Exception e) {
            Notification.show(" Eroare la refresh: " + e.getMessage(),
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }
}