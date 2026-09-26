/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Prestamo;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Joel
 */
public interface PrestamoDao {

    public JSONArray cargar();
    public void guardar(JSONArray prestamos);
    public void agregar(Prestamo prestamo);
    public boolean creardb();
    public boolean modificar(JSONObject prestamo);
    
}
