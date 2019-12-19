/*
 * SIP Shoftphone
 * This program is simple extention for SIMRSKhanza
 * 
 */
package siprsdh;

import AESsecurity.EnkripsiAES;
import fungsi.WarnaTable;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import fungsi.koneksiDB;
import fungsi.validasi;
import fungsi.var;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.sql.Connection;
import java.util.Properties;

import java.net.SocketException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import net.sourceforge.peers.Config;
import net.sourceforge.peers.FileLogger;
import net.sourceforge.peers.JavaConfig;
import net.sourceforge.peers.Logger;
import net.sourceforge.peers.media.AbstractSoundManager;
import net.sourceforge.peers.media.AbstractSoundManagerFactory;
import net.sourceforge.peers.media.MediaMode;
import net.sourceforge.peers.media.javaxsound.JavaxSoundManager;
import net.sourceforge.peers.rtp.RFC4733;
import net.sourceforge.peers.sip.RFC3261;
import net.sourceforge.peers.sip.Utils;

import net.sourceforge.peers.sip.core.useragent.SipListener;
import net.sourceforge.peers.sip.core.useragent.UserAgent;
import net.sourceforge.peers.sip.syntaxencoding.SipHeaderFieldName;
import net.sourceforge.peers.sip.syntaxencoding.SipUriSyntaxException;
import net.sourceforge.peers.sip.transactionuser.Dialog;
import net.sourceforge.peers.sip.transactionuser.DialogManager;
import net.sourceforge.peers.sip.transport.SipRequest;
import net.sourceforge.peers.sip.transport.SipResponse;


/**
 *
 * @author rizki999 IT RSDH
 */
public class DlgRSDHExtentionPhone extends javax.swing.JDialog implements SipListener, AbstractSoundManagerFactory, PhoneSoundListener {

    /**
     * Creates new form DlgRSDHKoutaDokter
     */
    public DlgRSDHExtentionPhone(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        phoneSound = new PhoneSound(this);
        btnCall.setMaximumSize(
                new Dimension(
                        500, 
                        500
                )
        );
                
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        MsgDisplay.init();
        
        tbMode = new DefaultTableModel(
                null,
                new Object[] {
                    "Unit Telepon", 
                    "extensi"
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
        
        setPhoneToRing(false);
        logger = new FileLogger("setting"+ File.separator + "phone.log");
        
        tbOnline.setModel(tbMode);
        
        tbOnline.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbOnline.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        tbOnline.getColumnModel().getColumn(0).setPreferredWidth(120);
        tbOnline.getColumnModel().getColumn(1).setPreferredWidth(80);
        
        tbOnline.setDefaultRenderer(Object.class, new WarnaTable());
        
        tglReg = "";
        
        dataExtentions = new ArrayList<>();
        
        sipRequest = null;
        sipResponse = null;
        
        this.DIALOG_HEIGHT_MAX = parent.getSize().height - 15;
        this.parentWH = new Dimension(
                parent.getSize().width, 
                this.DIALOG_HEIGHT_MAX
        );
        this.parentXY = new Point(0, 0);
        
        setSize(
                new Dimension(this.DIALOG_WIDTH, DIALOG_HEIGHT_MAX)
        );
        setLocation(parentXY.x +(parentWH.width - DIALOG_WIDTH), parentXY.y);
        setResizable(false);
        
        setEnabledPhone(false);
        
        panelBiasa.setPreferredSize(panBiasa);
        panelBiasa.repaint();
        
        SwingUtilities.invokeLater(new Runnable() { 
            public void run() { 
                // perform any operation 
                try {
                    PreparedStatement pr = koneksiDB.condb().prepareStatement("SELECT phone_name, phone_num FROM phone_extentions");
                    ResultSet data = pr.executeQuery();
                    while (data.next()) {
                        dataExtentions.add(
                                new DataExtention(
                                        data.getString("phone_name"), 
                                        data.getString("phone_num")
                                )
                        );
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                tampil();
                
                setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); 
                btnMinMax.setText("Minimalis");

                if (prop.getProperty("SIP_PHONE").equalsIgnoreCase("aktif")) {

                    setPhoneToRing(false);

                    try {
                        SIP_NUM = prop.getProperty(
                                "SIP_NUM"
                        );
                        SIP_DISPLAY_NAME = prop.getProperty(
                                "SIP_DISPLAY_NAME"
                        );
                        String sipIp = prop.getProperty(
                                "SIP_IP"
                        );
                        String sipUser = EnkripsiAES.decrypt(
                                prop.getProperty("SIP_USER")
                        );
                        String sipPass = EnkripsiAES.decrypt(
                                prop.getProperty("SIP_PASS")
                        );
                        String sipSrvHost = EnkripsiAES.decrypt(
                                prop.getProperty("SIP_SRV_HOST")
                        );
                        String sipSrvPort = EnkripsiAES.decrypt(
                                prop.getProperty("SIP_SRV_PORT")
                        );

                        soundManager = new JavaxSoundManager(false, logger, null);            


                        conf = new JavaConfig();
                        conf.setAuthorizationUsername(sipUser);
                        conf.setUserPart(sipUser);
                        conf.setPassword(sipPass);
                        conf.setDomain(sipSrvHost);
                        conf.setLocalInetAddress(InetAddress.getByName(sipIp));
                        conf.setMediaDebug(false);
                        conf.setSipPort(Integer.parseInt(sipSrvPort));
                        conf.setMediaMode(MediaMode.captureAndPlayback);            

                        userAgent = new UserAgent(thisObject, conf, logger);
                        userAgent.register();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (SipUriSyntaxException e) {
                        e.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    setEnabledPhone(true);
                }
                
                thisObject.setVisible(true);
            } 
        }); 
    }
    
    private final DefaultTableModel tbMode;
    private final Properties prop = new Properties();
    private String URL;
    private String idJadwal;
    private Connection cnn(){ return koneksiDB.condb(); }
    private validasi valid = new validasi();
    private String tglReg = "";
    private validasi Valid=new validasi();
    private ArrayList<DataExtention> dataExtentions;
    private Point parentXY;
    private Dimension parentWH;
    
    
    private final int DIALOG_WIDTH = 390;
    private final int DIALOG_HEIGHT_MIN = 170;
    private final int DIALOG_HEIGHT_MAX;
    private final Dimension panBiasa = new Dimension(588, 50);
    
    private DlgRSDHExtentionPhone thisObject = this;
    
    public UserAgent userAgent;
    public Config conf;
    private AbstractSoundManager soundManager;
    private SipRequest sipRequest;
    private SipResponse sipResponse;
    private PhoneSound phoneSound;
    private Logger logger;
    
    private String SIP_NUM;
    private String SIP_DISPLAY_NAME;
    
    /*private String SIP_STAND_BY = "Stand By";
    private String SIP_UNREGISTERED = "SIP Not Registered";
    private String SIP_INCOMING_CALL = "Incoming Call";
    private String SIP_ONGOING_CALL = "Ongoing Call";
    private String SIP_BUSY = "Busy";
    private String SIP_DISCONNECTED = "WAN Disconnected Lurd !";
    private String SIP_DIALING = "Dialing...";*/

    @Override 
    public void registering(SipRequest sipRequest) {
           setRegisterStatus("Registering...");
    }

    @Override 
    public void registerSuccessful(SipResponse sipResponse) { 
        this.sipResponse = sipResponse;
        setRegisterStatus("Registered (" + this.SIP_DISPLAY_NAME + " \"" + this.SIP_NUM + "\")");
        
    }

    @Override 
    public void registerFailed(SipResponse sipResponse) { 
        setRegisterStatus("Filed to register");
    }
    @Override 
    public void incomingCall(SipRequest sipRequest, SipResponse provResponse) { 
        this.sipRequest =  sipRequest;
        this.sipResponse = provResponse;
        phoneSound.stop();
        
        if (!this.isVisible()) {
            this.setVisible(true);
        }
        
        System.out.println("incomingCall fire " + getCleanFrom(this.sipResponse.getSipHeaders().get(new SipHeaderFieldName(RFC3261.HDR_FROM)).getValue()));
        
        setPhoneToRing(
                true
        );
        setStatusPhone(
                "Incoming Call !", 
                getCleanFrom(
                        this.sipResponse.getSipHeaders().get(
                                new SipHeaderFieldName(
                                        RFC3261.HDR_FROM
                                )
                        ).getValue()
                ), 100
        );
    }

    @Override 
    public void remoteHangup(SipRequest sipRequest) { 
        System.out.println("remoteHangup fire " + sipRequest.getMethod());
        setPhoneToRing(
                false
        );
    }

    @Override 
    public void ringing(SipResponse sipResponse) { 
        System.out.println("ringing fire");
        this.sipResponse = sipResponse;
    }

    @Override 
    public void calleePickup(SipResponse sipResponse) { 
        System.out.println("calleePickup fire");
        this.sipResponse = sipResponse;
        phoneSound.stop();
        btnCall.setText("Hangup");
        setStatusPhone(
                "Ongoing call", 
                getCleanFrom(
                        sipResponse.getSipHeaders().get(
                                new SipHeaderFieldName(
                                        RFC3261.HDR_FROM
                                )
                        ).getValue()
                ), 
                100
        );
    }

    @Override 
    public void error(SipResponse sipResponse) { 
        System.out.println("error fire " + sipResponse.getReasonPhrase() + " sipCode : " + sipResponse.getStatusCode());
        setPhoneToRing(false);
        phoneSound.stop();
        if (sipResponse.getStatusCode() == RFC3261.CODE_486_BUSYHERE) {
            phoneSound.play(
                    PhoneSound.BUSY, 
                    4
            );
            setStatusPhone(
                    "Busy lur euy di pareman/aya nu nelepon", 
                    getCleanFrom(
                            sipResponse.getSipHeaders().get(
                                    new SipHeaderFieldName(
                                            RFC3261.HDR_FROM
                                    )
                            ).getValue()
                    ), 
                    100
            );
            setEnabledPhone(false);
            return;
        }
        if (sipResponse.getStatusCode() == RFC3261.CODE_487_REQUEST_TERMINATED) {
            setStatusPhone(
                    "Geus", 
                    getCleanFrom(sipResponse.getSipHeaders().get(
                            new SipHeaderFieldName(
                                    RFC3261.HDR_FROM
                            )
                        ).getValue()
                    )
            );
            phoneSound.play(4);
            return;
        }
        phoneSound.play(
                PhoneSound.BUSY, 
                4
        );
        setStatusPhone("Busy lur euy teuing kunaon", getCleanFrom(sipResponse.getSipHeaders().get(new SipHeaderFieldName(RFC3261.HDR_FROM)).getValue()), 100);
        setEnabledPhone(false);
    }

    @Override
    public void dtmfEvent(RFC4733.DTMFEvent dtmfe, int i) {
        System.out.println("dtmfEvent fire");
    }
    
    @Override
    public AbstractSoundManager getSoundManager() {
        return soundManager;
    }
    
    @Override
    public void stopedPlay() {
        setStatusPhone("", "");
        setEnabledPhone(true);
    }
    public void setStatusPhone(String statusL1, String statusL2, int delay) {
        try {
            Thread.sleep(delay);
        } catch (Exception exIn) {
            exIn.printStackTrace();
        }
        setStatusPhone(statusL1, statusL2);
    }
    
    public void setStatusPhone(String statusL1, String statusL2) {
        MsgDisplay.line1 = statusL1;
        MsgDisplay.line2 = statusL2;
        txtMonitor.setText("");
        txtMonitor.setText(
                statusL1 + "\n" +
                statusL2 + "\n" +
                MsgDisplay.line3 
        );
    }
    
    public void setRegisterStatus(String status) {
        MsgDisplay.line3 = status;
        txtMonitor.setText("");
        txtMonitor.setText(
                MsgDisplay.line1 + "\n" +
                MsgDisplay.line2 + "\n" +
                status
        );
    }
    
    private String getURISip(String num) {
        return "sip:"+num+"@" + conf.getDomain();
    }
    
    public String getCleanFrom(String val) {
        String result = "";
        try {
                result = val.substring(0, val.indexOf("<")) + val.substring(val.indexOf(":") + 1, val.indexOf("@"));
        } catch (Exception ex) {
                ex.printStackTrace();
        }
        return result;

    }
    
    public String getNameOfExt(String ext) {
        String res = "Unknown";
        
        for (int i = 0; i < dataExtentions.size(); ++i) {
            if (dataExtentions.get(i).phone_num.equals(ext)){
                res = dataExtentions.get(i).phone_name;
            }
        }
        
        return res + " (" + ext + ")"; 
    }
    
    private void setPhoneToRing(boolean val) {
        if (val) {
            btnCall.setSize( new Dimension(
                    70,
                    btnCall.getSize().height

            ));
            btnReject.setVisible(true);
            btnCall.setText("Pickup");
            phoneSound.play(PhoneSound.RINGING);
            return;
        } 
        btnCall.setSize( new Dimension(
                140,
                btnCall.getSize().height

        ));
        btnCall.setText("Call");
        btnReject.setVisible(false);
        phoneSound.stop();
        setStatusPhone("", "");
    }
    
    private void setEnabledPhone(boolean val) {
        btnCall.setEnabled(val);
        btnReject.setEnabled(val);
        btnShutdown.setEnabled(val);
        txtPhoneNum.setEditable(val);
    }

    public void tampil() {
        try {
            tbMode.setRowCount(0);
            for (int i = 0; i < dataExtentions.size(); ++i) {
                if (dataExtentions.get(i).phone_name.toLowerCase().contains(txtCari.getText().toLowerCase()) || dataExtentions.get(i).phone_num.toLowerCase().contains(txtCari.getText().toLowerCase())) {
                    if (prop.getProperty("SIP_PHONE").equalsIgnoreCase("aktif")) { 
                        if(!dataExtentions.get(i).phone_num.equalsIgnoreCase(prop.getProperty("SIP_NUM")))
                            tbMode.addRow(
                                new Object[] {
                                    dataExtentions.get(i).phone_name,
                                    dataExtentions.get(i).phone_num
                                }
                            );
                    } else
                        tbMode.addRow(
                            new Object[] {
                                dataExtentions.get(i).phone_name,
                                dataExtentions.get(i).phone_num
                            }
                        );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void emptText() {
        txtPhoneName.setText("-");
        txtPhoneNum.setText("-");
        tglReg = "";
    }
    
    public String getNoRawat() {
        return txtPhoneName.getText();
    }
    
    public String getTglRegOnline() {
        return tglReg;
    }
    
    public void Call(String num) {
        if (btnCall.getText().equalsIgnoreCase("Call")) {
            txtPhoneNum.setText(num);
            btnCallActionPerformed(null);
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

        internalFrame2 = new widget.InternalFrame();
        panelBiasa1 = new widget.PanelBiasa();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtMonitor = new javax.swing.JTextPane();
        txtPhoneName = new widget.TextBox();
        txtPhoneNum = new widget.TextBox();
        btnCall = new widget.Button();
        btnReject = new widget.Button();
        btnMinMax = new widget.Button();
        btnHideShow = new widget.Button();
        btnShutdown = new widget.Button();
        panelBiasa = new widget.PanelBiasa();
        jLabel6 = new widget.Label();
        txtCari = new widget.TextBox();
        BtnCari = new widget.Button();
        scrollPane1 = new widget.ScrollPane();
        tbOnline = new widget.Table();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setLocation(this.getParent().getLocation().x +(this.getParent().getSize().width - this.getSize().width), this.getParent().getLocation().y);
        setSize(new java.awt.Dimension(378, 83));

        internalFrame2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "::[SIMRSKhanza Shoftphone]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(130, 100, 100))); // NOI18N
        internalFrame2.setToolTipText("");
        internalFrame2.setName(""); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 0));

        panelBiasa1.setPreferredSize(new java.awt.Dimension(588, 120));
        panelBiasa1.setLayout(null);

        txtMonitor.setEditable(false);
        txtMonitor.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtMonitor.setPreferredSize(new java.awt.Dimension(36, 5));
        StyledDocument doc = txtMonitor.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        jScrollPane1.setViewportView(txtMonitor);

        panelBiasa1.add(jScrollPane1);
        jScrollPane1.setBounds(5, 0, 370, 70);

        txtPhoneName.setEditable(false);
        txtPhoneName.setText("-");
        txtPhoneName.setToolTipText("");
        panelBiasa1.add(txtPhoneName);
        txtPhoneName.setBounds(5, 70, 160, 24);

        txtPhoneNum.setToolTipText("");
        txtPhoneNum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPhoneNumKeyPressed(evt);
            }
        });
        panelBiasa1.add(txtPhoneNum);
        txtPhoneNum.setBounds(170, 70, 70, 24);

        btnCall.setText("Call");
        btnCall.setMaximumSize(new java.awt.Dimension(300, 300));
        btnCall.setPreferredSize(new java.awt.Dimension(49, 28));
        btnCall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCallActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnCall);
        btnCall.setBounds(240, 73, 70, 20);

        btnReject.setText("Reject");
        btnReject.setPreferredSize(new java.awt.Dimension(62, 28));
        btnReject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRejectActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnReject);
        btnReject.setBounds(310, 73, 62, 20);

        btnMinMax.setMnemonic('K');
        btnMinMax.setText("Detail");
        btnMinMax.setToolTipText("");
        btnMinMax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMinMaxActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnMinMax);
        btnMinMax.setBounds(0, 100, 100, 20);

        btnHideShow.setMnemonic('K');
        btnHideShow.setText("Sembunyikan");
        btnHideShow.setToolTipText("Alt+K");
        btnHideShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHideShowActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnHideShow);
        btnHideShow.setBounds(110, 100, 100, 20);

        btnShutdown.setMnemonic('K');
        btnShutdown.setText("Matikan");
        btnShutdown.setToolTipText("Alt+K");
        btnShutdown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShutdownActionPerformed(evt);
            }
        });
        panelBiasa1.add(btnShutdown);
        btnShutdown.setBounds(250, 100, 100, 20);

        internalFrame2.add(panelBiasa1, java.awt.BorderLayout.NORTH);

        panelBiasa.setPreferredSize(new java.awt.Dimension(588, 50));
        panelBiasa.setLayout(new java.awt.GridBagLayout());

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Cari");
        jLabel6.setPreferredSize(new java.awt.Dimension(30, 23));
        panelBiasa.add(jLabel6, new java.awt.GridBagConstraints());

        txtCari.setPreferredSize(new java.awt.Dimension(290, 23));
        txtCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCariActionPerformed(evt);
            }
        });
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCariKeyPressed(evt);
            }
        });
        panelBiasa.add(txtCari, new java.awt.GridBagConstraints());

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('7');
        BtnCari.setToolTipText("Alt+7");
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelBiasa.add(BtnCari, new java.awt.GridBagConstraints());

        internalFrame2.add(panelBiasa, java.awt.BorderLayout.SOUTH);

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

        internalFrame2.add(scrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void txtCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }
    }//GEN-LAST:event_txtCariKeyPressed

    private void txtCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCariActionPerformed

    private void tbOnlineMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbOnlineMouseClicked
        if (tbOnline.getSelectedRow() != -1) {
            txtPhoneName.setText(
                tbOnline.getValueAt(
                    tbOnline.getSelectedRow(),
                    0
                ).toString()
            );
            txtPhoneNum.setText(
                tbOnline.getValueAt(
                    tbOnline.getSelectedRow(),
                    1
                ).toString()
            );
        }
    }//GEN-LAST:event_tbOnlineMouseClicked

    private void btnRejectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRejectActionPerformed
        // TODO add your handling code here:
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    userAgent.rejectCall(sipRequest);
                    sipRequest = null;
                    setPhoneToRing(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        phoneSound.stop();
    }//GEN-LAST:event_btnRejectActionPerformed

    private void btnShutdownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShutdownActionPerformed
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    if (userAgent.isRegistered())
                    userAgent.unregister();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    userAgent.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    userAgent = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(3 * RFC3261.TIMER_T1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dispose();
            }
        });
    }//GEN-LAST:event_btnShutdownActionPerformed

    private void btnHideShowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHideShowActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_btnHideShowActionPerformed
    
    private void btnMinMaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMinMaxActionPerformed
        this.setResizable(true);
        if (btnMinMax.getText().equalsIgnoreCase("Detail")) {
            this.setSize(
                new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT_MAX)
            );
            btnMinMax.setText("Minimalis");
            this.setLocation(parentXY.x + (parentWH.width - DIALOG_WIDTH), parentXY.y);
            
            panelBiasa.setPreferredSize(panBiasa);
            panelBiasa.repaint();
        } else {
            this.setSize(
                new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT_MIN)
            );
            btnMinMax.setText("Detail");
            this.setLocation(parentXY.x +(parentWH.width - DIALOG_WIDTH), parentWH.height - (DIALOG_HEIGHT_MIN));
            
            panelBiasa.setPreferredSize(new Dimension(0, 0));
            panelBiasa.repaint();
        }
        try {
            Thread.sleep(50);
        } catch (Exception e) { }
        this.setResizable(false);
    }//GEN-LAST:event_btnMinMaxActionPerformed

    private void btnCallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCallActionPerformed

        if (btnCall.getText().equalsIgnoreCase("Call")) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        sipRequest = userAgent.invite(
                            getURISip(txtPhoneNum.getText()),
                            Utils.generateCallID(
                                conf.getLocalInetAddress()
                            )
                        );
                        String name = getNameOfExt(txtPhoneNum.getText());
                        setStatusPhone("Dialing...", name);
                        txtPhoneName.setText(name);
                        phoneSound.play(PhoneSound.DIALING);
                        btnCall.setText("Hangup");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } else if (btnCall.getText().equalsIgnoreCase("Hangup")) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        userAgent.terminate(sipRequest);
                        btnCall.setText("Call");
                        sipRequest = null;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            setPhoneToRing(false);
        } else if (btnCall.getText().equalsIgnoreCase("Pickup")) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        String callId = Utils.getMessageCallId(sipRequest);
                        DialogManager dialogManager = userAgent.getDialogManager();
                        Dialog dialog = dialogManager.getDialog(callId);
                        userAgent.acceptCall(
                            sipRequest,
                            dialog
                        );
                        setPhoneToRing(false);
                        btnCall.setText("Hangup");
                        setStatusPhone("Ongoing call", getCleanFrom(sipResponse.getSipHeaders().get(new SipHeaderFieldName(RFC3261.HDR_FROM)).getValue()));
                        sipRequest = null;
                        sipResponse = null;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }//GEN-LAST:event_btnCallActionPerformed

    private void txtPhoneNumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhoneNumKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            if (!btnCall.getText().equalsIgnoreCase("Call"))
            return;
            btnCallActionPerformed(null);
        }

    }//GEN-LAST:event_txtPhoneNumKeyPressed

    
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
            java.util.logging.Logger.getLogger(DlgRSDHExtentionPhone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DlgRSDHExtentionPhone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DlgRSDHExtentionPhone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DlgRSDHExtentionPhone.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
                DlgRSDHExtentionPhone dialog = new DlgRSDHExtentionPhone(new javax.swing.JFrame(), true);
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
    private widget.Button btnCall;
    private widget.Button btnHideShow;
    private widget.Button btnMinMax;
    private widget.Button btnReject;
    private widget.Button btnShutdown;
    private widget.InternalFrame internalFrame2;
    private widget.Label jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private widget.PanelBiasa panelBiasa;
    private widget.PanelBiasa panelBiasa1;
    private widget.ScrollPane scrollPane1;
    private widget.Table tbOnline;
    private widget.TextBox txtCari;
    private javax.swing.JTextPane txtMonitor;
    private widget.TextBox txtPhoneName;
    private widget.TextBox txtPhoneNum;
    // End of variables declaration//GEN-END:variables
    
    private class DataExtention {
        public DataExtention(String phoneName, String phoneNum) {
            this.phone_name = phoneName;
            this.phone_num = phoneNum;
        }
        
        public String phone_name;
        public String phone_num;
    }
    
    private static class MsgDisplay {
        public static void init() {
            line1 = "";
            line2 = "";
            line3 = "";
        }
        public static String line1;
        public static String line2;
        public static String line3;
    }    
}
