package com.unla.travelweb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tipo_habitacion")
public class TipoHabitacion {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
	private long id;
	
    @Column(name="tipo")
	private String tipo;
	
    @Column(name="porcentaje_aumento")
	private double porcentajeAumento;

	public TipoHabitacion(long id, String tipo, double porcentajeAumento) {
		super();
		this.id = id;
		this.tipo = tipo;
		this.porcentajeAumento = porcentajeAumento;
	}

	public TipoHabitacion(String tipo, double porcentajeAumento) {
		super();
		this.tipo = tipo;
		this.porcentajeAumento = porcentajeAumento;
	}

	public TipoHabitacion() {}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public double getPorcentajeAumento() {
		return porcentajeAumento;
	}

	public void setPorcentajeAumento(double porcentajeAumento) {
		this.porcentajeAumento = porcentajeAumento;
	}
    
    
	
}
