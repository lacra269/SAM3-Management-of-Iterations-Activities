package com.dam.pms.views;

import com.dam.pms.domain.entity.TeamMember;
import com.dam.pms.domain.enums.MemberRole;
import com.dam.pms.ui.service.TeamMemberUIService;
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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.dam.pms.config.ServiceLocator;

@Route("team-members")
public class TeamMemberView extends VerticalLayout {

    private final TeamMemberUIService teamMemberUIService;
    private Grid<TeamMember> grid;
    private Binder<TeamMember> binder;

    public TeamMemberView() {
        this.teamMemberUIService = ServiceLocator.getTeamMemberUIService();

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

        H2 title = new H2("Membrii Echipei");

        header.add(backButton, title);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        return header;
    }

    private HorizontalLayout createToolbar() {
        Button addButton = new Button("Adaugă Membru");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openMemberDialog(new TeamMember()));

        ComboBox<MemberRole> roleFilter = new ComboBox<>("Filtrare după rol");
        roleFilter.setItems(MemberRole.values());
        roleFilter.setItemLabelGenerator(MemberRole::name);
        roleFilter.setClearButtonVisible(true);
        roleFilter.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                grid.setItems(teamMemberUIService.findByRole(e.getValue()));
            } else {
                refreshGrid();
            }
        });

        Checkbox activeOnly = new Checkbox("Doar activi");
        activeOnly.addValueChangeListener(e -> {
            if (e.getValue()) {
                grid.setItems(teamMemberUIService.findActive());
            } else {
                refreshGrid();
            }
        });

        HorizontalLayout toolbar = new HorizontalLayout(addButton, roleFilter, activeOnly);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.START);
        return toolbar;
    }

    private Grid<TeamMember> createGrid() {
        Grid<TeamMember> grid = new Grid<>(TeamMember.class, false);

        grid.addColumn(TeamMember::getId).setHeader("ID").setWidth("80px");
        grid.addColumn(TeamMember::getName).setHeader("Nume").setFlexGrow(1);
        grid.addColumn(TeamMember::getEmail).setHeader("Email").setFlexGrow(1);
        grid.addColumn(TeamMember::getRole).setHeader("Rol");
        grid.addComponentColumn(member -> {
            Span badge = new Span(member.getIsActive() ? "Activ" : "Inactiv");
            badge.getElement().getThemeList().add(
                    member.getIsActive() ? "badge success" : "badge error"
            );
            return badge;
        }).setHeader("Status");

        grid.addComponentColumn(member -> {
            Button editBtn = new Button("Editează");
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editBtn.addClickListener(e -> openMemberDialog(member));

            Button deleteBtn = new Button("Șterge");
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> confirmDelete(member));

            return new HorizontalLayout(editBtn, deleteBtn);
        }).setHeader("Acțiuni");

        grid.setSizeFull();
        return grid;
    }

    private void openMemberDialog(TeamMember member) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(member.getId() == null ? "Membru Nou" : "Editare Membru");
        dialog.setWidth("500px");

        FormLayout formLayout = new FormLayout();

        TextField nameField = new TextField("Nume");
        EmailField emailField = new EmailField("Email");
        ComboBox<MemberRole> roleField = new ComboBox<>("Rol");
        roleField.setItems(MemberRole.values());
        roleField.setItemLabelGenerator(MemberRole::name);

        Checkbox activeField = new Checkbox("Activ");

        formLayout.add(nameField, emailField, roleField, activeField);

        binder = new Binder<>(TeamMember.class);
        binder.forField(nameField).asRequired("Numele este obligatoriu")
                .bind(TeamMember::getName, TeamMember::setName);
        binder.forField(emailField).asRequired("Email-ul este obligatoriu")
                .bind(TeamMember::getEmail, TeamMember::setEmail);
        binder.forField(roleField).asRequired("Rolul este obligatoriu")
                .bind(TeamMember::getRole, TeamMember::setRole);
        binder.bind(activeField, TeamMember::getIsActive, TeamMember::setIsActive);

        binder.readBean(member);

        Button saveButton = new Button("Salvează", e -> {
            if (binder.validate().isOk()) {
                try {
                    binder.writeBean(member);
                    teamMemberUIService.save(member);
                    refreshGrid();
                    dialog.close();
                    showNotification("Membru salvat cu succes!", NotificationVariant.LUMO_SUCCESS);
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

    private void confirmDelete(TeamMember member) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirmare ștergere");
        dialog.setText("Sigur doriți să ștergeți membrul: " + member.getName() + "?");

        dialog.setCancelable(true);
        dialog.setCancelText("Anulează");

        dialog.setConfirmText("Șterge");
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(e -> {
            teamMemberUIService.delete(member.getId());
            refreshGrid();
            showNotification("Membru șters!", NotificationVariant.LUMO_SUCCESS);
        });

        dialog.open();
    }

    private void refreshGrid() {
        grid.setItems(teamMemberUIService.findAll());
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }
}