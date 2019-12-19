/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simrskhanza;

import com.mysql.jdbc.MySQLConnection;
import fungsi.OneInstanceDialog;
import java.awt.Dimension;
import fungsi.WarnaTable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.var;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import kepegawaian.DlgCariDokter;

/**
 *
 * @author rizki999
 */
public class DlgRSDHCallCaneledPatien extends javax.swing.JDialog {

    /**
     * Creates new form DlgRSDHKoutaDokter
     */
    public DlgRSDHCallCaneledPatien(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        tbMode = new DefaultTableModel(
                null,
                new Object[] {
                    "No. Rawat", 
                    "No. RM", 
                    "Nama Pasien", 
                    "", 
                    "Dokter",
                    "Poliklinik",
                    "No Telepone"
                } ) {
             @Override 
             public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
             }
             
             @Override
             public Class getColumnClass(int columnIndex) {
                return Object.class;
             }
        };
        
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {;}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){                    
                    kddokter = dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString();
                    txtDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                    tampil();
                }
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
        tbOnline.setModel(tbMode);
        
        tbOnline.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbOnline.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        tbOnline.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbOnline.getColumnModel().getColumn(1).setPreferredWidth(80);
        tbOnline.getColumnModel().getColumn(2).setPreferredWidth(200);
        
        tbOnline.getColumnModel().getColumn(3).setMaxWidth(0);
        tbOnline.getColumnModel().getColumn(3).setMinWidth(0);
        tbOnline.getColumnModel().getColumn(3).setPreferredWidth(0);
        
        tbOnline.getColumnModel().getColumn(4).setPreferredWidth(120);
        tbOnline.getColumnModel().getColumn(5).setPreferredWidth(80);
        tbOnline.getColumnModel().getColumn(6).setPreferredWidth(200);
        
        tbOnline.setDefaultRenderer(Object.class, new WarnaTable());
        
        tglReg = "";
        parentSiZe = Toolkit.getDefaultToolkit().getScreenSize();
        kddokter = "";
        tampil();
    }
    
    private final DefaultTableModel tbMode;
    private String idJadwal;
    private Dimension parentSiZe;
    private Connection cnn = koneksiDB.condb();
    private validasi valid = new validasi();
    private String tglReg = "";
    private validasi Valid=new validasi();
    private sekuel Sequel=new sekuel();
    public  DlgCariDokter dokter=new DlgCariDokter(null,false); 
    
    private String kddokter;
    
    public void tampil() {
        try {
            tbMode.setRowCount(0);
            
            PreparedStatement ps = cnn.prepareStatement(
                    "SELECT " +
                        "rp.no_rawat, " +
                        "p.no_rkm_medis, " +
                        "p.nm_pasien, " +
                        "d.kd_dokter, " +
                        "d.nm_dokter, " +
                        "pk.nm_poli, " +
                        "p.no_tlp " +
                    "FROM " +
                            "reg_periksa AS rp " +
                    "LEFT JOIN " +
                            "pasien AS p " +
                    "ON " +
                            "p.no_rkm_medis=rp.no_rkm_medis " +
                    "LEFT JOIN " +
                            "dokter AS d " +
                    "ON " +
                            "d.kd_dokter=rp.kd_dokter " +
                    "LEFT JOIN " +
                            "poliklinik AS pk " +
                    "ON " +
                            "pk.kd_poli=rp.kd_poli " +
                    
                    "WHERE " +
                            "rp.tgl_registrasi = '" + valid.SetTgl(DTPCari1.getSelectedItem().toString()) + "' " +
                    "AND " +
                            "rp.kd_dokter='" + kddokter + "';"
            );
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                tbMode.addRow(
                    new Object[] {
                        rs.getString(1) == null ? "" : rs.getString(1),
                        rs.getString(2) == null ? "" : rs.getString(2),
                        rs.getString(3) == null ? "" : rs.getString(3),
                        rs.getString(4) == null ? "" : rs.getString(4),
                        rs.getString(5) == null ? "" : rs.getString(5),
                        rs.getString(6) == null ? "" : rs.getString(6),
                        rs.getString(7) == null ? "" : rs.getString(7)
                    }
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void emptText() {
        txtNama.setText("-");
        txtNoPhone.setText("-");
        tglReg = "";
        kddokter = "";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        panelBiasa1 = new widget.PanelBiasa();
        txtNoPhone = new widget.TextBox();
        txtNama = new widget.TextBox();
        jLabel4 = new widget.Label();
        btnCall = new widget.Button();
        cbPreNum = new widget.ComboBox();
        scrollPane1 = new widget.ScrollPane();
        tbOnline = new widget.Table();
        panelBiasa2 = new widget.PanelBiasa();
        jLabel14 = new widget.Label();
        txtDokter = new widget.TextBox();
        BtnSeek3 = new widget.Button();
        jLabel15 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        BtnCari = new widget.Button();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "::[Telepon Pasien Yang Batal]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(130, 100, 100))); // NOI18N
        internalFrame1.setToolTipText("");
        internalFrame1.setName(""); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 0));

        panelBiasa1.setPreferredSize(new java.awt.Dimension(588, 90));
        panelBiasa1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNoPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNoPhoneKeyPressed(evt);
            }
        });
        panelBiasa1.add(txtNoPhone, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, 210, -1));

        txtNama.setEditable(false);
        txtNama.setToolTipText("");
        panelBiasa1.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 390, -1));

        jLabel4.setText("Pasien :");
        panelBiasa1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 20));

        btnCall.setText("Call");
        btnCall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCallActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnCall, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 40, 200, 23));

        cbPreNum.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "8", "9" }));
        cbPreNum.setPreferredSize(new java.awt.Dimension(133, 23));
        cbPreNum.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbPreNumItemStateChanged(evt);
            }
        });
        panelBiasa1.add(cbPreNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 10, 60, -1));

        internalFrame1.add(panelBiasa1, java.awt.BorderLayout.NORTH);

        scrollPane1.setPreferredSize(new java.awt.Dimension(100, 50));

        tbOnline.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbOnline.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbOnlineMouseClicked(evt);
            }
        });
        scrollPane1.setViewportView(tbOnline);

        internalFrame1.add(scrollPane1, java.awt.BorderLayout.CENTER);

        panelBiasa2.setPreferredSize(new java.awt.Dimension(588, 50));

        jLabel14.setText("Dokter :");
        jLabel14.setPreferredSize(new java.awt.Dimension(60, 23));
        panelBiasa2.add(jLabel14);

        txtDokter.setEditable(false);
        txtDokter.setPreferredSize(new java.awt.Dimension(300, 23));
        panelBiasa2.add(txtDokter);

        BtnSeek3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeek3.setMnemonic('6');
        BtnSeek3.setToolTipText("ALt+6");
        BtnSeek3.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnSeek3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeek3ActionPerformed(evt);
            }
        });
        panelBiasa2.add(BtnSeek3);

        jLabel15.setText("Periode :");
        jLabel15.setPreferredSize(new java.awt.Dimension(60, 23));
        panelBiasa2.add(jLabel15);

        DTPCari1.setEditable(false);
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "29-11-2019" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(133, 23));
        panelBiasa2.add(DTPCari1);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('7');
        BtnCari.setToolTipText("Alt+7");
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelBiasa2.add(BtnCari);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelBiasa2.add(BtnKeluar);

        internalFrame1.add(panelBiasa2, java.awt.BorderLayout.SOUTH);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        internalFrame1.getAccessibleContext().setAccessibleName("::[Kouta Pasien Dokter]::");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNoPhoneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNoPhoneKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNoPhoneKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void tbOnlineMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbOnlineMouseClicked
        if (tbOnline.getSelectedRow() != -1) {
            txtNama.setText(
                    tbOnline.getValueAt(
                            tbOnline.getSelectedRow(), 
                            2
                    ).toString()
            );
            txtNoPhone.setText(
                    tbOnline.getValueAt(
                            tbOnline.getSelectedRow(), 
                            6
                    ).toString()
            );
            
            kddokter = tbOnline.getValueAt(
                            tbOnline.getSelectedRow(), 
                            3
            ).toString();
        }
    }//GEN-LAST:event_tbOnlineMouseClicked

    private void btnCallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCallActionPerformed
        if (txtNoPhone.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Pilih nomber pasiennya dahulu !");
            return;
        }
        
        if (OneInstanceDialog.dlgRSDHExtentionPhone != null) {
           OneInstanceDialog.dlgRSDHExtentionPhone.setVisible(true);
           OneInstanceDialog.dlgRSDHExtentionPhone.Call(cbPreNum.getSelectedItem().toString() + txtNoPhone.getText());
        } else
            JOptionPane.showMessageDialog(null, "Nyalakan dulu teleponya !");
          
    }//GEN-LAST:event_btnCallActionPerformed

    private void cbPreNumItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbPreNumItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cbPreNumItemStateChanged

    private void BtnSeek3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeek3ActionPerformed
        dokter.isCek();
        dokter.TCari.requestFocus();
        dokter.setSize(parentSiZe.width - 40, parentSiZe.height - 40);
        dokter.setLocationRelativeTo(this);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnSeek3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DlgRSDHCallCaneledPatien.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DlgRSDHCallCaneledPatien.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DlgRSDHCallCaneledPatien.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DlgRSDHCallCaneledPatien.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DlgRSDHCallCaneledPatien dialog = new DlgRSDHCallCaneledPatien(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Button BtnSeek3;
    private widget.Tanggal DTPCari1;
    private widget.Button btnCall;
    private widget.ComboBox cbPreNum;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel4;
    private widget.PanelBiasa panelBiasa1;
    private widget.PanelBiasa panelBiasa2;
    private widget.ScrollPane scrollPane1;
    private widget.Table tbOnline;
    private widget.TextBox txtDokter;
    private widget.TextBox txtNama;
    private widget.TextBox txtNoPhone;
    // End of variables declaration//GEN-END:variables
}
