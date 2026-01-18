package com.dam.pms.views;

import com.dam.pms.domain.entity.Project;
import com.dam.pms.domain.entity.Risk;
import com.dam.pms.domain.enums.Priority;
import com.dam.pms.ui.service.RiskUIService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.dam.pms.config.ServiceLocator;

@Route("risks")
public class RiskView extends VerticalLayout {

    private final RiskUIService riskUIService;
    private Grid<Risk> grid;
    private Binder<Risk> binder;

    public RiskView() {
        this.riskUIService = ServiceLocator.getRiskUIService();

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

        H2 title = new H2("Gestiune Riscuri");

        header.add(backButton, title);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        return header;
    }

    private HorizontalLayout createToolbar() {
        Button addButton = new Button("Adaugă Risc");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openRiskDialog(new Risk()));

        ComboBox<Project> projectFilter = new ComboBox<>("Filtrare după proiect");
        projectFilter.setItems(ServiceLocator.getProjectUIService().findAll());
        projectFilter.setItemLabelGenerator(Project::getName);
        projectFilter.setClearButtonVisible(true);
        projectFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                grid.setItems(riskUIService.findByProject(e.getValue()));
            } else {
                refreshGrid();
            }
        });

        Checkbox unresolvedOnly = new Checkbox("Doar nerezolvate");
        unresolvedOnly.addValueChangeListener(e -> {
            if (e.getValue()) {
                grid.setItems(riskUIService.findUnresolved());
            } else {
                refreshGrid();
            }
        });

        HorizontalLayout toolbar = new HorizontalLayout(addButton, projectFilter, unresolvedOnly);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.START);
        return toolbar;
    }

    private Grid<Risk> createGrid() {
        Grid<Risk> grid = new Grid<>(Risk.class, false);

        grid.addColumn(Risk::getId).setHeader("ID").setWidth("80px");
        grid.addColumn(Risk::getDescription).setHeader("Descriere").setFlexGrow(2);
        grid.addColumn(risk -> risk.getProject() != null ? risk.getProject().getName() : "N/A")
                .setHeader("Proiect");
        grid.addColumn(Risk::getSeverity).setHeader("Severitate");

        grid.addComponentColumn(risk -> {
            Span badge = new Span(risk.isResolved() ? "Rezolvat" : "Nerezolvat");
            badge.getElement().getThemeList().add(
                    risk.isResolved() ? "badge success" : "badge error"
            );
            return badge;
        }).setHeader("Status");

        grid.addComponentColumn(risk -> {
            HorizontalLayout actions = new HorizontalLayout();

            if (!risk.isResolved()) {
                Button resolveBtn = new Button("Rezolvă");
                resolveBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
                resolveBtn.addClickListener(e -> {
                    riskUIService.resolve(risk.getId());
                    refreshGrid();
                    showNotification("Risc rezolvat!", NotificationVariant.LUMO_SUCCESS);
                });
                actions.add(resolveBtn);
            }

            Button editBtn = new Button("Editează");
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editBtn.addClickListener(e -> openRiskDialog(risk));

            Button deleteBtn = new Button("Șterge");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> confirmDelete(risk));

            actions.add(editBtn, deleteBtn);
            return actions;
        }).setHeader("Acțiuni");

        grid.setSizeFull();
        return grid;
    }

    private void openRiskDialog(Risk risk) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(risk.getId() == null ? "Risc Nou" : "Editare Risc");
        dialog.setWidth("600px");

        FormLayout formLayout = new FormLayout();

        TextArea descriptionField = new TextArea("Descriere");
        descriptionField.setWidthFull();

        ComboBox<Project> projectField = new ComboBox<>("Proiect");
        projectField.setItems(ServiceLocator.getProjectUIService().findAll());
        projectField.setItemLabelGenerator(Project::getName);

        ComboBox<Priority> severityField = new ComboBox<>("Severitate");
        severityField.setItems(Priority.values());
        severityField.setItemLabelGenerator(Priority::name);

        formLayout.add(descriptionField, projectField, severityField);
        formLayout.setColspan(descriptionField, 2);

        binder = new Binder<>(Risk.class);
        binder.forField(descriptionField).asRequired("Descrierea este obligatorie")
                .bind(Risk::getDescription, Risk::setDescription);
        binder.forField(projectField).asRequired("Proiectul este obligatoriu")
                .bind(Risk::getProject, Risk::setProject);
        binder.forField(severityField).asRequired("Severitatea este obligatorie")
                .bind(Risk::getSeverity, Risk::setSeverity);

        binder.readBean(risk);

        Button saveButton = new Button("Salvează", e -> {
            if (binder.validate().isOk()) {
                try {
                    binder.writeBean(risk);
                    if (risk.getId() == null) {
                        risk.setResolved(false); // Inițializare pentru risc nou
                    }
                    riskUIService.save(risk);
                    refreshGrid();
                    dialog.close();
                    showNotification("Risc salvat cu succes!", NotificationVariant.LUMO_SUCCESS);
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

    private void confirmDelete(Risk risk) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirmare ștergere");
        dialog.setText("Sigur doriți să ștergeți acest risc?");

        dialog.setCancelable(true);
        dialog.setCancelText("Anulează");

        dialog.setConfirmText("Șterge");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> {
            riskUIService.delete(risk.getId());
            refreshGrid();
            showNotification("Risc șters!", NotificationVariant.LUMO_SUCCESS);
        });

        dialog.open();
    }

    private void refreshGrid() {
        grid.setItems(riskUIService.findAll());
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }
}