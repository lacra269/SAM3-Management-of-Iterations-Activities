
package com.dam.pms.views;

import com.dam.pms.config.ServiceLocator;
import com.dam.pms.domain.entity.Iteration;
import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.enums.IterationStatus;
import com.dam.pms.domain.enums.ProjectStatus;
import com.dam.pms.ui.service.ProjectUIService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

@Route("projects")
public class ProjectView extends VerticalLayout {

    private final ProjectUIService projectUIService;
    private Grid<Project> grid;
    private Binder<Project> binder;

    public ProjectView() {
        this.projectUIService = ServiceLocator.getProjectUIService();

        setSizeFull();

        HorizontalLayout header = createHeader();
        HorizontalLayout toolbar = createToolbar();
        grid = createGrid();

        add(header, toolbar, grid);
        refreshGrid();
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();

        Button backButton = new Button("← Înapoi");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(MainView.class)));

        H2 title = new H2("Gestiune Proiecte");

        header.add(backButton, title);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        return header;
    }

    private HorizontalLayout createToolbar() {
        Button addButton = new Button("Adaugă Proiect");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openProjectDialog(new Project()));

        ComboBox<ProjectStatus> statusFilter = new ComboBox<>("Filtrare după status");
        statusFilter.setItems(ProjectStatus.values());
        statusFilter.setItemLabelGenerator(ProjectStatus::name);
        statusFilter.setClearButtonVisible(true);
        statusFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                grid.setItems(projectUIService.findByStatus(e.getValue()));
            } else {
                refreshGrid();
            }
        });

        HorizontalLayout toolbar = new HorizontalLayout(addButton, statusFilter);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        return toolbar;
    }

    private Grid<Project> createGrid() {
        Grid<Project> grid = new Grid<>(Project.class, false);
        grid.addColumn(Project::getId).setHeader("ID").setWidth("80px");
        grid.addColumn(Project::getName).setHeader("Nume").setFlexGrow(1);
        grid.addColumn(Project::getDescription).setHeader("Descriere").setFlexGrow(2);
        grid.addColumn(Project::getStatus).setHeader("Status");
        grid.addColumn(project -> {
            Double progress = project.getProgress();
            return String.format("%.1f%%", progress != null ? progress : 0.0);
        }).setHeader("Progres");

        grid.addComponentColumn(project -> {
            Button editBtn = new Button("Editează");
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editBtn.addClickListener(e -> openProjectDialog(project));

            Button deleteBtn = new Button("Șterge");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> confirmDelete(project));

            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Acțiuni");

        grid.setSizeFull();
        return grid;
    }

    private void openProjectDialog(Project project) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(project.getId() == null ? "Proiect Nou" : "Editare Proiect");
        dialog.setWidth("650px");

        FormLayout formLayout = new FormLayout();

        // --- CAMPURI PROIECT ---
        TextField nameField = new TextField("Nume");
        TextArea descriptionField = new TextArea("Descriere");
        descriptionField.setWidthFull();

        ComboBox<ProjectStatus> statusField = new ComboBox<>("Status");
        statusField.setItems(ProjectStatus.values());
        statusField.setItemLabelGenerator(ProjectStatus::name);

        // --- CAMPURI ITERATIE ---
        TextField iterationName = new TextField("Nume iterație");
        TextField iterationNumber = new TextField("Număr iterație");

        ComboBox<IterationStatus> iterationStatus = new ComboBox<>("Status iterație");
        iterationStatus.setItems(IterationStatus.values());
        iterationStatus.setItemLabelGenerator(IterationStatus::name);

        formLayout.add(
                nameField,
                statusField,
                descriptionField,
                iterationName,
                iterationNumber,
                iterationStatus
        );
        formLayout.setColspan(descriptionField, 2);

        binder = new Binder<>(Project.class);
        binder.forField(nameField).asRequired("Numele este obligatoriu")
                .bind(Project::getName, Project::setName);
        binder.bind(descriptionField, Project::getDescription, Project::setDescription);
        binder.bind(statusField, Project::getStatus, Project::setStatus);

        binder.readBean(project);

        Button saveButton = new Button("Salvează", e -> {
            if (binder.validate().isOk()) {
                try {
                    binder.writeBean(project);

                    if (project.getProgress() == null) {
                        project.setProgress(0.0);
                    }

                    // --- CREARE ITERATIE DOAR LA PROIECT NOU ---
                    if (project.getId() == null) {
                        Iteration iteration = new Iteration();
                        iteration.setName(iterationName.getValue());
                        iteration.setNumber(Integer.parseInt(iterationNumber.getValue()));
                        iteration.setStatus(iterationStatus.getValue());
                        iteration.setProject(project);

                        project.getIterations().add(iteration);
                    }

                    projectUIService.save(project);
                    refreshGrid();
                    dialog.close();
                    showNotification("Proiect salvat cu succes!", NotificationVariant.LUMO_SUCCESS);

                } catch (Exception ex) {
                    showNotification("Eroare la salvare: " + ex.getMessage(), NotificationVariant.LUMO_ERROR);
                }
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Anulează", e -> dialog.close());

        dialog.add(formLayout);
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void confirmDelete(Project project) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirmare ștergere");
        dialog.setText("Sigur doriți să ștergeți proiectul: " + project.getName() + "?");

        dialog.setCancelable(true);
        dialog.setCancelText("Anulează");

        dialog.setConfirmText("Șterge");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> {
            projectUIService.delete(project.getId());
            refreshGrid();
            showNotification("Proiect șters!", NotificationVariant.LUMO_SUCCESS);
        });

        dialog.open();
    }

    private void refreshGrid() {
        grid.setItems(projectUIService.findAll());
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }
}
