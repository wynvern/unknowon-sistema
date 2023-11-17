/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package view;

import classes.ComboItem;
import static classes.ConverterData.converterEmData;
import dao.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.Entidades;

/**
 *
 * @author wynvern
 */
public class JIFPDV extends javax.swing.JInternalFrame {
    
    private Entidades getEntidade(int idPesquisa) {
        DataSource dataSource = new DataSource();
        Entidades result = null;

        try {
            String SQL = "SELECT * FROM entidades WHERE id = ?;";

            try (PreparedStatement ps = dataSource.getConnection().prepareStatement(SQL)) {
                ps.setInt(1, idPesquisa);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result = new Entidades();
                        result.setId(rs.getInt("id"));
                        result.setNome(rs.getString("nome"));
                        result.setTipo(rs.getString("tipo"));
                        result.setTelefone(rs.getString("telefone"));
                        result.setEmail(rs.getString("email"));
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JIFPDV.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }
    
    private void atualizarTabelaNotas(String codigoPesquisa, String idEntidadePesquisa, Timestamp dataInicial, Timestamp dataFinal, Boolean isPendurado) {
        DataSource dataSource = new DataSource();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        try {
            for (int i = model.getRowCount() - 1; i >= 0; i--) {
                model.removeRow(i);
            }
            // HANDLE BOTH DATES
            StringBuilder SQLBuilder = new StringBuilder("SELECT * FROM notas WHERE (id = ? OR ? = '') AND (idEntidade = ? OR ? = '') AND (tipo = ?)");

            if (isPendurado != null) {
                SQLBuilder.append(" AND (pendurado = ? OR ? IS NULL)");
            }
            
            if (dataInicial != null && dataFinal != null) {
                SQLBuilder.append(" AND (data BETWEEN ? AND ?)");
            } else if (dataInicial != null) {
                SQLBuilder.append(" AND (data >= ?)");
            } else if (dataFinal != null) {
                SQLBuilder.append(" AND (data <= ?)");
            }

            try (PreparedStatement ps = dataSource.getConnection().prepareStatement(SQLBuilder.toString())) {
                ps.setString(1, codigoPesquisa);
                ps.setString(2, codigoPesquisa);
                ps.setString(3,  idEntidadePesquisa );
                ps.setString(4, idEntidadePesquisa );
                ps.setString(5, "Saida");

                int indexNow = 6;
                
                if (isPendurado != null) {
                    ps.setBoolean(indexNow++, isPendurado);
                    ps.setBoolean(indexNow++, isPendurado);
                }
                
                if (dataInicial != null && dataFinal != null) {
                    ps.setTimestamp(indexNow++, dataInicial);
                    ps.setTimestamp(indexNow++, dataFinal);
                } else if (dataInicial != null) {
                    ps.setTimestamp(indexNow++, dataInicial);
                } else if (dataFinal != null) {
                    ps.setTimestamp(indexNow++, dataFinal);
                }
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Entidades buscaEntidade = getEntidade(rs.getInt("idEntidade"));
                        model.addRow(new Object[]{rs.getInt("id"), buscaEntidade.getNome(), rs.getString("data"), rs.getString("precoTotal"), rs.getBoolean("pendurado") ? "Sim" : "Não"});
                    }
                    rs.close();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            dataSource.closeDataSource();
        }
    }
    
    private void atualizarListaClientes(String searchId, String searchDesc) {
        DataSource dataSource = new DataSource();
        PreparedStatement ps = null;
        try{
            clientesBox.removeAllItems();
            if (searchId.equals("") && searchDesc.equals("")) {
                ComboItem emptyItem = new ComboItem("Selecione uma opção...", "");
                clientesBox.addItem(emptyItem);
            }
            String SQL = "SELECT * FROM entidades WHERE (id = ? OR ? = '') AND (nome LIKE ? OR ? LIKE '') AND (tipo = ?)";
            
            try {
                ps = dataSource.getConnection().prepareStatement(SQL);
                ps.setString(1, (searchId));
                ps.setString(2, (searchId));
                ps.setString(3, "%" + searchDesc + "%");
                ps.setString(4, searchDesc);
                ps.setString(5, "Cliente");
                
            } catch (SQLException ex) {
                Logger.getLogger(JIFProdutos.class.getName()).log(Level.SEVERE, null, ex);
            }

            ResultSet rs = null;
            try {
                rs = ps.executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(JIFProdutos.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            while(rs.next()){
                ComboItem comboItem = new ComboItem(rs.getString("nome") + "   -   Código: " + rs.getString("id"), rs.getString("id"));
                clientesBox.addItem(comboItem);
            }
            if (!searchId.equals("") || !searchDesc.equals("")) {
                ComboItem emptyItem = new ComboItem("Selecione uma opção...", "");
                clientesBox.addItem(emptyItem);
            }
            
            try {
                // fecha o statement e o datasource
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataSource.closeDataSource();
    }   catch (SQLException ex) {
            Logger.getLogger(JIFClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * Creates new form JIFProdutos
     */
    public JIFPDV() {
        initComponents();
        atualizarListaClientes("", "");
        atualizarTabelaNotas("", "", null, null, null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        dataInicial = new javax.swing.JFormattedTextField();
        dataFinal = new javax.swing.JFormattedTextField();
        clientesBox = new javax.swing.JComboBox<>();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        pendurado = new javax.swing.JToggleButton();
        jButton3 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("PDV");
        setToolTipText("");
        setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/images/vender.png"))); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pesquisar.png"))); // NOI18N
        jButton1.setText("Pesquisar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/naoPesquisa.png"))); // NOI18N
        jButton2.setText("Limpar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Pessoa.png"))); // NOI18N
        jLabel1.setText("Cliente:");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        jLabel2.setText("Data Inicial:");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/calendar.png"))); // NOI18N
        jLabel3.setText("Data Final:");

        try {
            dataInicial.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        try {
            dataFinal.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pesquisar.png"))); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fechar.png"))); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        pendurado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cartao.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clientesBox, 0, 380, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dataFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pendurado)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(dataInicial)
                    .addComponent(dataFinal)
                    .addComponent(jLabel3)
                    .addComponent(clientesBox)
                    .addComponent(jButton7)
                    .addComponent(jButton6)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jButton2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jButton1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(pendurado)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Sair.png"))); // NOI18N
        jButton3.setText("Sair");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/vender.png"))); // NOI18N
        jButton4.setText("Iniciar Venda");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/recarregar.png"))); // NOI18N
        jButton5.setText("Atualizar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cartao.png"))); // NOI18N
        jButton8.setText("Marcar como Paga");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Cliente", "Data", "Preço Total", "Em aberto"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(table);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton3))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        JFSistema.abrirJIFVender();
        JFSistema.venderOpened = true;
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.dispose();
        JFSistema.PDVOpened = false;
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        atualizarTabelaNotas("", "", null, null, null);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        String valuePesquisa = JOptionPane.showInputDialog(null, "Nome do cliente:");
        String idPesquisa = JOptionPane.showInputDialog(null, "Id do cliente:");
        
        if (valuePesquisa.isEmpty()) valuePesquisa = "";
        if (idPesquisa.isEmpty()) idPesquisa = "";
        
        System.out.println(idPesquisa);
        System.out.println(valuePesquisa);

        atualizarListaClientes(idPesquisa, valuePesquisa);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        atualizarListaClientes("", "");
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        atualizarListaClientes("", "");
        dataInicial.setText("");
        dataFinal.setText("");
        atualizarTabelaNotas("", "", null, null, null);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Timestamp inicialD = null;
        Boolean isPendurado = pendurado.isSelected();
        Timestamp finalD = null;
        if (!dataInicial.getText().equals("  /  /  ")) {
            inicialD = converterEmData(dataInicial.getText());
        }
        if (!dataFinal.getText().equals("  /  /  ")) {
            finalD = converterEmData(dataFinal.getText());
        }

        ComboItem selectedComboItem = (ComboItem) clientesBox.getSelectedItem();
        String value = selectedComboItem.getValue();

        atualizarTabelaNotas("", value, inicialD, finalD, isPendurado);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        int row = table.getSelectedRow();
        Boolean statusAtual = "Sim".equalsIgnoreCase(table.getValueAt(row, 4).toString()) ? true : false;
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Nenhuma coluna selecionada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (statusAtual == false) {
            JOptionPane.showMessageDialog(null, "A nota selecionada já foi paga.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int opcao = JOptionPane.showConfirmDialog(null,"Deseja realmente marcar como paga?","Confirmação",JOptionPane.YES_OPTION);
        if(opcao == JOptionPane.YES_OPTION) {
            DataSource dataSource = new DataSource();
            Connection con = dataSource.getConnection();
            PreparedStatement ps = null;

            try{
                String SQL = "UPDATE notas SET pendurado=? WHERE id=?";

                ps = con.prepareStatement(SQL);
                ps.setInt(2, Integer.parseInt(table.getValueAt(row, 0).toString()));
                ps.setBoolean(1, false);

                ps.executeUpdate();
                ps.close();
            }
            catch (SQLException ex){
                //System.err.println("Erro ao salvar os dados "+ex.getMessage());
                JOptionPane.showMessageDialog(null,"Erro ao alterar!\n"+ex);
            }
            finally{
                // fecha o statement e o datasource
                dataSource.closeDataSource();
            }
        }
        atualizarTabelaNotas("", "", null, null, null);
    }//GEN-LAST:event_jButton8ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<ComboItem> clientesBox;
    private javax.swing.JFormattedTextField dataFinal;
    private javax.swing.JFormattedTextField dataInicial;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton pendurado;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
