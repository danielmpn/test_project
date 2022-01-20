/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edamatec_project.dao;

import edamatec_project.connection.MySql;
import edamatec_project.models.Cliente;
import edamatec_project.views.Clientes_View;
import edamatec_project.views.ERP_View;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author danie
 */
public class ClienteDAO {

    public void create(Cliente cliente) {
        //camada 1: Validação
        if (cliente.getNome().trim().equals("")
                || cliente.getCpf().trim().equals("")
                || cliente.getTelefone().trim().equals("")
                || cliente.getEmail().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Existem campos vazios");
            return;
        } else if (cliente.getCpf().length() < 14
                || cliente.getTelefone().length() < 15) {
            JOptionPane.showMessageDialog(null, "Verifique se preencheu os campos corretamente");
            return;
        } else if (cpfExists(cliente.getCpf())) {
            JOptionPane.showMessageDialog(null, "CPF já cadastrado");
            return;
        }

        //camada 2: Inserção
        String sql = "INSERT INTO tb_clientes(nome,cpf,telefone,email) VALUES (?,?,?,?)";

        PreparedStatement ps;

        try {
            ps = MySql.getCon().prepareStatement(sql);
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setString(3, cliente.getTelefone());
            ps.setString(4, cliente.getEmail());
            ps.execute();
            if (ps != null) {
                ps.close();
            }

            JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!");
            ERP_View.showView(new Clientes_View("all", null));
        } catch (SQLException ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Algo deu errado, contate Daniel Pantuffi");
        }
    }

    public boolean destroy(int id) {
        PreparedStatement ps = null;
        try {
            Object[] options = {"Sim", "Não"};
            int confirm = JOptionPane.showOptionDialog(null, "Tem certeza que deseja excluir o cliente selecionado?", null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            if (confirm == 0) {
                String sql = "DELETE FROM tb_clientes WHERE id=?";

                ps = MySql.getCon().prepareStatement(sql);
                ps.setInt(1, id);
                ps.execute();

                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }

    public boolean update(Cliente cliente) {
        //camada 1: Validação
        if (cliente.getNome().trim().equals("")
                || cliente.getCpf().trim().equals("")
                || cliente.getTelefone().trim().equals("")
                || cliente.getEmail().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Existem campos vazios");
            return false;
        } else if (cliente.getCpf().length() < 14
                || cliente.getTelefone().length() < 15) {
            JOptionPane.showMessageDialog(null, "Verifique se preencheu os campos corretamente");
            return false;
        } else if (cpfExists(cliente.getCpf(), cliente.getId())) {
            JOptionPane.showMessageDialog(null, "CPF já cadastrado");
            return false;
        }

        //Camada 2: possível atualização
        PreparedStatement ps = null;
        try {
            Object[] options = {"Sim", "Não"};
            int confirm = JOptionPane.showOptionDialog(null, "Tem certeza que deseja salvar?", null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
            if (confirm == 0) {

                String sql = "UPDATE tb_clientes SET nome=?, email=?, telefone=?, cpf=? WHERE id=?";

                ps = MySql.getCon().prepareStatement(sql);
                ps.setString(1, cliente.getNome());
                ps.setString(2, cliente.getEmail());
                ps.setString(3, cliente.getTelefone());
                ps.setString(4, cliente.getCpf());
                ps.setInt(5, cliente.getId());
                ps.execute();

                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }

    public List<Cliente> find(String campo, String texto) {

        List<Cliente> clientes = new ArrayList<Cliente>();
        ResultSet rs = null;

        try {
            String sql = null;

            if (texto == null || texto.trim().equals("")) {
                sql = "SELECT * FROM tb_clientes;";
                PreparedStatement ps = MySql.getCon().prepareStatement(sql);
                rs = ps.executeQuery();
            } else if (campo.equals("id")) {
                sql = "SELECT * FROM tb_clientes WHERE id LIKE ?;";
                PreparedStatement ps = MySql.getCon().prepareStatement(sql);
                ps.setString(1, texto);
                rs = ps.executeQuery();
            } else if (campo.equals("nome")) {
                sql = "SELECT * FROM tb_clientes WHERE nome LIKE ?;";
                PreparedStatement ps = MySql.getCon().prepareStatement(sql);
                ps.setString(1, "%" + texto + "%");
                rs = ps.executeQuery();
            } else if (campo.equals("email")) {
                sql = "SELECT * FROM tb_clientes WHERE email LIKE ?;";
                PreparedStatement ps = MySql.getCon().prepareStatement(sql);
                ps.setString(1, "%" + texto + "%");
                rs = ps.executeQuery();
            } else if (campo.equals("cpf")) {
                sql = "SELECT * FROM tb_clientes WHERE cpf LIKE ?;";
                PreparedStatement ps = MySql.getCon().prepareStatement(sql);
                ps.setString(1, "%" + texto + "%");
                rs = ps.executeQuery();
            } else if (campo.equals("telefone")) {
                sql = "SELECT * FROM tb_clientes WHERE telefone LIKE ?;";
                PreparedStatement ps = MySql.getCon().prepareStatement(sql);
                ps.setString(1, "%" + texto + "%");
                rs = ps.executeQuery();
            }

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setCpf(rs.getString("cpf"));
                cliente.setNome(rs.getString("nome"));
                cliente.setTelefone(rs.getString("telefone"));
                cliente.setEmail(rs.getString("email"));

                clientes.add(cliente);
            };

            return clientes;
        } catch (SQLException ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public boolean cpfExists(String cpf) {
        try {
            List<Cliente> clientes = new ArrayList<Cliente>();

            String sql = "SELECT cpf FROM tb_clientes WHERE cpf=?";
            PreparedStatement ps = MySql.getCon().prepareStatement(sql);
            ps.setString(1, cpf);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setCpf(rs.getString("cpf"));

                clientes.add(cliente);
            };

            if (ps != null) {
                ps.close();
            }

            if (rs != null) {
                rs.close();
            }
            if (clientes.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public boolean cpfExists(String cpf, int id) {
        try {
            List<Cliente> clientes = new ArrayList<Cliente>();

            String sql = "SELECT cpf FROM tb_clientes WHERE cpf=? AND id!=?";
            PreparedStatement ps = MySql.getCon().prepareStatement(sql);
            ps.setString(1, cpf);
            ps.setInt(2, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setCpf(rs.getString("cpf"));

                clientes.add(cliente);
            };

            if (ps != null) {
                ps.close();
            }

            if (rs != null) {
                rs.close();
            }
            if (clientes.size() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClienteDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

}
