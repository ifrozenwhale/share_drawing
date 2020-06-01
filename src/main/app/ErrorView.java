/*
 * Created by JFormDesigner on Wed Apr 01 22:46:22 CST 2020
 */

package main.app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Tian runze
 * 不存在异常
 */
public class ErrorView extends JDialog {
    public ErrorView(Frame owner) {
        super(owner);
        initComponents();
    }

    public ErrorView(Dialog owner) {
        super(owner);
        initComponents();
    }
    public ErrorView(String info){
        initComponents();
        errorLabel.setText(info);
    }
    private void okButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void initComponents() {
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        errorLabel = new JLabel();
        buttonBar = new JPanel();
        okButton = new JButton();

        //======== this ========
        setBackground(new Color(214, 41, 0));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setBackground(Color.white);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setBackground(Color.white);
                contentPanel.setLayout(new GridLayout(1, 1));

                //---- errorLabel ----
                errorLabel.setText("\u53d1\u751f\u9519\u8bef\uff01\u4e0d\u5b58\u5728\uff01");
                errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                errorLabel.setFont(new Font("Arial Unicode MS", errorLabel.getFont().getStyle(), errorLabel.getFont().getSize() + 5));
                errorLabel.setBorder(null);
                errorLabel.setBackground(new Color(214, 41, 0));
                errorLabel.setForeground(Color.red);
                errorLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                errorLabel.setMaximumSize(new Dimension(151, 50));
                errorLabel.setMinimumSize(new Dimension(151, 50));
                errorLabel.setPreferredSize(new Dimension(151, 50));
                contentPanel.add(errorLabel);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setBackground(Color.white);
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                okButton.setBorderPainted(false);
                okButton.setBackground(Color.red);
                okButton.setForeground(Color.white);
                okButton.setFocusCycleRoot(true);
                okButton.addActionListener(this::okButtonActionPerformed);
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
    }
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel errorLabel;
    private JPanel buttonBar;
    private JButton okButton;

}
