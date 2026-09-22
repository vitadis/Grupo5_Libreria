/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import org.json.JSONObject;

/**
 *
 * @author Joel
 */
public interface PrestamoDao {

    public void guardar(JSONObject json);
    public JSONObject cargar();
    
}
