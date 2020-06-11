package com.unla.travelweb.entities;

import java.sql.Date;

import javax.persistence.*;


@Entity
@Table(name="reserva_actividad")
public class ReservaActividad {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;
	
	@Column(name="nombre", nullable=false)
	private String nombre;
	
	@Column(name="fecha", nullable=true)
	private Date fecha;

	@Column (name= "valoracion", nullable=true)
	private double valoracion;
	
    @Column(name="accesibilidad", nullable=false)
    private boolean accesibilidad;
    
	@ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="destino_id")
    private Destino destino;

	@Column (name= "precio", nullable=false)
	private double precio;
	
	@Column(name= "img_path", nullable = true )
	private String imgPath;
	
    public ReservaActividad() {}
    
    public ReservaActividad(String nombre,Date fecha, double valoracion, boolean accesibilidad, Destino destino, double precio, String imgPath) {
		super();
		this.nombre = nombre;
		this.fecha = fecha;
		this.valoracion = valoracion;
		this.accesibilidad = accesibilidad;
		this.destino = destino;
		this.precio = precio;
		this.imgPath = imgPath;
	}
    
	public ReservaActividad(long id, String nombre, Date fecha, double valoracion, boolean accesibilidad, Destino destino, double precio, String imgPath) {
		super();
		this.nombre = nombre;
		this.id = id;
		this.fecha = fecha;
		this.valoracion = valoracion;
		this.accesibilidad = accesibilidad;
		this.destino = destino;
		this.precio = precio;
		this.imgPath = imgPath;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Destino getDestino() {
		return destino;
	}

	public void setDestino(Destino destino) {
		this.destino = destino;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public double getValoracion() {
		return valoracion;
	}

	public void setValoracion(double valoracion) {
		this.valoracion = valoracion;
	}

	public boolean isAccesibilidad() {
		return accesibilidad;
	}

	public void setAccesibilidad(boolean accesibilidad) {
		this.accesibilidad = accesibilidad;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	
	
	
}
