/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package sistema.adopciones;

import sistema.custom.TableBadgeCellRenderer;
import sistema.custom.TableHeaderAlignment;
import sistema.custom.CheckBoxTableHeaderRenderer;
import sistema.custom.CustomPaginationItemRender;
import sistema.animales.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import pagination.EventPagination;
import pagination.PaginationItemRender;
import raven.modal.ModalDialog;
import raven.modal.Toast;
import raven.modal.component.SimpleModalBorder;
import raven.popup.DefaultOption;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.toast.Notifications;
import sistema.dao.AdopcionDAO;
import sistema.dao.AnimalDAO;
import sistema.modelos.Adopcion;
import sistema.modelos.Adopcion.EstadoAdopcion;
import sistema.modelos.Animal;
import sistema.modelos.Usuario;

/**
 *
 * @author Fer
 */
public class PrincipalAdopciones extends javax.swing.JPanel {
    private static final int REGISTROS_POR_PAGINA = 10;
    private String terminoBusqueda = ""; 
    private Usuario usuarioActual;
    
    /**
     * Creates new form Principal
     */
    public PrincipalAdopciones(Usuario usuarioActual) {
        initComponents();
        this.usuarioActual = usuarioActual;
        SwingUtilities.invokeLater(() -> {
            initializeGlassPanePopup();
        });
        Notifications.getInstance().setJFrame((JFrame) SwingUtilities.getWindowAncestor(this));
        panel.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background");
        
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {
                "SELECT", "ID", "Adoptante", "Mascota", "Contacto", "Fecha Solicitud", 
                "Estado", "Fecha Resolución", "Motivo Rechazo", "Entrega", "Fecha Seguimiento"
            }
        ) {
            final Class[] types = new Class [] {
                Boolean.class, Object.class, Object.class, Object.class, Object.class, Object.class,
                EstadoAdopcion.class, Object.class, Object.class, Object.class, Object.class
            };
            final boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false, false, false, false
            };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "height:30;"
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background;"
                + "font:bold;");

        table.putClientProperty(FlatClientProperties.STYLE, ""
                + "rowHeight:70;"
                + "showHorizontalLines:true;"
                + "intercellSpacing:0,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");

        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "trackArc:999;"
                + "trackInsets:3,3,3,3;"
                + "thumbInsets:3,3,3,3;"
                + "background:$Table.background;");

        //lbTitle.putClientProperty(FlatClientProperties.STYLE, ""
        //        + "font:bold +5;");

        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("icons/search.svg"));
        txtSearch.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:15;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "margin:5,20,5,20;"
                + "background:$Panel.background");
        
        
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setMaxWidth(40);

        table.getColumnModel().getColumn(0).setHeaderRenderer(new CheckBoxTableHeaderRenderer(table, 0));
        TableBadgeCellRenderer.apply(table, EstadoAdopcion.class, 100);
        //TableSimpleCellRenderer.apply(table, CondicionSalud.class, 100);
        table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table));
        table.getColumnModel().getColumn(3).setCellRenderer(new MascotaTableRenderer(table));
        table.getColumnModel().getColumn(4).setCellRenderer(new ContactoTableRenderer(table));
        
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        // Convertir índice de vista a modelo (importante si hay filtros/ordenamiento)
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        
                        System.out.println("Doble clic en fila: " + selectedRow);
                        System.out.println("Fila del modelo: " + modelRow);
                        
                        // Obtener datos de la fila
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        Object[] rowData = new Object[model.getColumnCount()];
                        for (int i = 0; i < model.getColumnCount(); i++) {
                            rowData[i] = model.getValueAt(modelRow, i);
                        }
                        
                        // Aquí puedes hacer lo que necesites con los datos
                        //handleDoubleClick(selectedRow, modelRow, rowData);
                    }
                }
            }
        });
        
        pagination1.setPaginationItemRender(new CustomPaginationItemRender());
        
        pagination1.addEventPagination(new EventPagination() {
            @Override
            public void pageChanged(int page) {
                loadData(page);
            }
        });

        try {
            loadData(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void initializeGlassPanePopup() {
        try {
            java.awt.Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                // Verificar que el frame no sea null y esté inicializado
                if (frame != null && frame.getContentPane() != null && 
                    frame.getHeight() > 0 && frame.getWidth() > 0) { // <- AGREGAR ESTA VERIFICACIÓN
                    GlassPanePopup.install(frame);
                    Notifications.getInstance().setJFrame(frame);
                    System.out.println("GlassPanePopup instalado correctamente");
                } else {
                    System.out.println("Frame no está completamente inicializado, reintentando...");
                    // Reintentar después de que el frame esté completamente cargado
                    SwingUtilities.invokeLater(() -> {
                        initializeGlassPanePopup();
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo instalar GlassPanePopup: " + e.getMessage());
            // No es crítico, el resto de la aplicación seguirá funcionando
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbTitle1 = new javax.swing.JLabel();
        panel = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jSeparator1 = new javax.swing.JSeparator();
        txtSearch = new javax.swing.JTextField();
        cmdDelete = new sistema.custom.ButtonAction();
        cmdEdit = new sistema.custom.ButtonAction();
        cmdNew = new sistema.custom.ButtonAction();
        jPanel1 = new javax.swing.JPanel();
        pagination1 = new pagination.Pagination();
        cmdAprobar = new sistema.custom.ButtonAction();
        cmdRechazar = new sistema.custom.ButtonAction();

        setBackground(new java.awt.Color(255, 255, 255));

        lbTitle1.setFont(new java.awt.Font("Poppins", 1, 20)); // NOI18N
        lbTitle1.setText("Gestión de adopciones");

        panel.setBackground(new java.awt.Color(255, 255, 255));

        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SELECT", "ID", "Adoptante", "Mascota", "Contacto", "Solicitud", "Estado", "Fecha Resolución", "Motivo Rechazo", "Fecha Entrega", "Fecha Seguimiento"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, true, false, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.getTableHeader().setReorderingAllowed(false);
        scroll.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setMaxWidth(50);
            table.getColumnModel().getColumn(1).setMaxWidth(40);
            table.getColumnModel().getColumn(2).setPreferredWidth(100);
            table.getColumnModel().getColumn(3).setPreferredWidth(100);
            table.getColumnModel().getColumn(4).setPreferredWidth(100);
            table.getColumnModel().getColumn(5).setMaxWidth(40);
            table.getColumnModel().getColumn(6).setPreferredWidth(100);
        }

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        cmdDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/delete.png"))); // NOI18N
        cmdDelete.setText("Eliminar");
        cmdDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDeleteActionPerformed(evt);
            }
        });

        cmdEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/edit.png"))); // NOI18N
        cmdEdit.setText("Editar");
        cmdEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdEditActionPerformed(evt);
            }
        });

        cmdNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"))); // NOI18N
        cmdNew.setText("Agregar");
        cmdNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdNewActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        pagination1.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(324, Short.MAX_VALUE)
                .addComponent(pagination1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(324, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pagination1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        cmdAprobar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-check-24.png"))); // NOI18N
        cmdAprobar.setText("Aprobar");
        cmdAprobar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdAprobarActionPerformed(evt);
            }
        });

        cmdRechazar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/close.png"))); // NOI18N
        cmdRechazar.setText("Rechazar");
        cmdRechazar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRechazarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmdAprobar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmdRechazar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(cmdNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addComponent(scroll, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1328, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdDelete, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdAprobar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdRechazar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 569, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(lbTitle1, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(lbTitle1)
                .addGap(27, 27, 27)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 91, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        searchData(txtSearch.getText().trim());
    }//GEN-LAST:event_txtSearchKeyReleased

    private void cmdDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDeleteActionPerformed
        List<Adopcion> list = getSelectedData();
        if (!list.isEmpty()) {
            SimpleModalBorder.Option[] options = new SimpleModalBorder.Option[]{
                new SimpleModalBorder.Option("Cancelar", SimpleModalBorder.CANCEL_OPTION),
                new SimpleModalBorder.Option("Eliminar", SimpleModalBorder.OK_OPTION)
            };
            
            JLabel label = new JLabel("¿Estás segur@ de eliminar " + list.size() + " registro(s) ?");
            label.setBorder(new EmptyBorder(5, 25, 5, 25));
            ModalDialog.showModal(this, new SimpleModalBorder(label, "Confirmar Eliminación", options, (mc, i) -> {
                if (i == SimpleModalBorder.OK_OPTION) {
                    try {
                        for (Adopcion adopcion : list) {
                            AdopcionDAO.eliminar(adopcion.getIdAdopcion());
                        }
                        showNotification(Notifications.Type.SUCCESS, "Registro(s) eliminado(s)");
                        loadData(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loadData(1);
                }
            }));
        } else {
            showNotification(Notifications.Type.WARNING, "Selecciona un registro a eliminar");
        }
    }//GEN-LAST:event_cmdDeleteActionPerformed

    private void cmdEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdEditActionPerformed
        List<Adopcion> list = getSelectedData();
        if (!list.isEmpty()) {
            if (list.size() == 1) {
                Adopcion data = list.get(0);
                Create create = new Create();
                create.loadData(data);
                
                SimpleModalBorder.Option[] options = new SimpleModalBorder.Option[]{
                    new SimpleModalBorder.Option("Cancelar", SimpleModalBorder.CANCEL_OPTION),
                    new SimpleModalBorder.Option("Actualizar", SimpleModalBorder.OK_OPTION)
                };
                ModalDialog.showModal(this, new SimpleModalBorder(create, "Editar Registro [" + data.getIdAdopcion()+ "]", options, (mc, i) -> {
                    if (i == SimpleModalBorder.OK_OPTION) {
                        try {
                            Adopcion dataEdit = create.getData();
                            dataEdit.setIdAdopcion(data.getIdAdopcion());
                            AdopcionDAO.actualizar(dataEdit);
                            showNotification(Notifications.Type.SUCCESS, "Registro actualizado");
                            loadData(1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (i == SimpleModalBorder.OPENED) {
                        create.init();
                    }
                }));
            } else {
                showNotification(Notifications.Type.WARNING, "Por favor selecciona sólo un registro");
            }
        } else {
            showNotification(Notifications.Type.WARNING, "Por favor selecciona un registro para editar");
        }
    }//GEN-LAST:event_cmdEditActionPerformed

    private void cmdNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdNewActionPerformed
        Create create = new Create();
        create.loadData(null);

        SimpleModalBorder.Option[] options = new SimpleModalBorder.Option[]{
            new SimpleModalBorder.Option("Cancelar", SimpleModalBorder.CANCEL_OPTION),
            new SimpleModalBorder.Option("Guardar", SimpleModalBorder.OK_OPTION)
        };
        ModalDialog.showModal(this, new SimpleModalBorder(create, "Agregar registro", options, (mc, i) -> {
            if (i == SimpleModalBorder.OK_OPTION) {
                try {
                    AdopcionDAO.guardar(create.getData());
                    showNotification(Notifications.Type.SUCCESS, "Registro agregado");
                    loadData(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (i == SimpleModalBorder.OPENED) {
                create.init();
            }
        }));
    }//GEN-LAST:event_cmdNewActionPerformed

    private void cmdAprobarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdAprobarActionPerformed
        aprobarAdopcion();
    }//GEN-LAST:event_cmdAprobarActionPerformed

    private void cmdRechazarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRechazarActionPerformed
        rechazarAdopcion();
    }//GEN-LAST:event_cmdRechazarActionPerformed

    private void loadData(int page) {
        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            model.setRowCount(0);

            List<Adopcion> adopciones;
            int totalRecords;

            if (!terminoBusqueda.isEmpty()) {
                adopciones = AdopcionDAO.buscarPorAdoptanteConPaginacion(terminoBusqueda, page, REGISTROS_POR_PAGINA);
                totalRecords = AdopcionDAO.contarPorAdoptante(terminoBusqueda);
            } else {
                adopciones = AdopcionDAO.obtenerConPaginacion(page, REGISTROS_POR_PAGINA);
                totalRecords = AdopcionDAO.contarTodos();
            }

            int totalPages = (int) Math.ceil((double) totalRecords / REGISTROS_POR_PAGINA);

            for (Adopcion adopcion : adopciones) {
                model.addRow(adopcion.toTableRow());
            }

            pagination1.setPagegination(page, totalPages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchData(String search) {
        try {
            terminoBusqueda = search.trim(); // Guardar término de búsqueda
            loadData(1); // Reiniciar en página 1 cuando se busca
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private List<Adopcion> getSelectedData() {
        List<Adopcion> list = new ArrayList<>();
        for (int i = 0; i < table.getRowCount(); i++) {
            if ((boolean) table.getValueAt(i, 0)) {
                Adopcion data = (Adopcion) AdopcionDAO.obtenerPorId((Integer)table.getValueAt(i, 1));
                list.add(data);
            }
        }
        return list;
    }
    
    /**
     * Método para mostrar notificaciones toast
     */
    private void showNotification(Notifications.Type type, String message) {
        try {
            if (Notifications.getInstance() != null) {
                Notifications.getInstance().show(type, Notifications.Location.TOP_CENTER, message);
            } else {
                // Fallback a JOptionPane si las notificaciones no están disponibles
                String title = type == Notifications.Type.SUCCESS ? "Éxito" :
                              type == Notifications.Type.WARNING ? "Advertencia" :
                              type == Notifications.Type.ERROR ? "Error" : "Información";
                int messageType = type == Notifications.Type.SUCCESS ? JOptionPane.INFORMATION_MESSAGE :
                                 type == Notifications.Type.WARNING ? JOptionPane.WARNING_MESSAGE :
                                 type == Notifications.Type.ERROR ? JOptionPane.ERROR_MESSAGE : 
                                 JOptionPane.INFORMATION_MESSAGE;
                JOptionPane.showMessageDialog(this, message, title, messageType);
            }
        } catch (Exception e) {
            // Último recurso: usar JOptionPane simple
            JOptionPane.showMessageDialog(this, message);
        }
    }
    
    private void aprobarAdopcion() {
        List<Adopcion> seleccionadas = getSelectedData();
        if (seleccionadas.size() == 1 && seleccionadas.get(0).getEstado() == EstadoAdopcion.PENDIENTE) {
            Adopcion adopcion = seleccionadas.get(0);
            Animal animal = AnimalDAO.obtenerPorId(adopcion.getAnimal().getIdAnimal());
            animal.setEstado(Animal.EstadoAnimal.ADOPTADO);
            AnimalDAO.actualizar(animal);
            adopcion.setEstado(EstadoAdopcion.APROBADA);
            adopcion.setAprobador(usuarioActual);
            adopcion.setFechaResolucion(LocalDate.now());
            AdopcionDAO.actualizar(adopcion);
            showNotification(Notifications.Type.SUCCESS, "Solicitud aprobada");
            loadData(1);
        } else {
            showNotification(Notifications.Type.WARNING, "Selecciona una solicitud pendiente");
        }
    }

    private void rechazarAdopcion() {
        List<Adopcion> seleccionadas = getSelectedData();
        if (seleccionadas.size() == 1 && seleccionadas.get(0).getEstado() == EstadoAdopcion.PENDIENTE) {
            String motivo = JOptionPane.showInputDialog(this, "Motivo del rechazo:", "Rechazar Adopción", JOptionPane.QUESTION_MESSAGE);
            if (motivo != null && !motivo.trim().isEmpty()) {
                Adopcion adopcion = seleccionadas.get(0);
                adopcion.setEstado(EstadoAdopcion.RECHAZADA);
                adopcion.setMotivoRechazo(motivo);
                adopcion.setAprobador(usuarioActual);
                adopcion.setFechaResolucion(LocalDate.now());
                AdopcionDAO.actualizar(adopcion);
                showNotification(Notifications.Type.SUCCESS, "Solicitud rechazada");
                loadData(1);
            }
        } else {
            showNotification(Notifications.Type.WARNING, "Selecciona una solicitud pendiente");
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private sistema.custom.ButtonAction cmdAprobar;
    private sistema.custom.ButtonAction cmdDelete;
    private sistema.custom.ButtonAction cmdEdit;
    private sistema.custom.ButtonAction cmdNew;
    private sistema.custom.ButtonAction cmdRechazar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbTitle1;
    private pagination.Pagination pagination1;
    private javax.swing.JPanel panel;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
