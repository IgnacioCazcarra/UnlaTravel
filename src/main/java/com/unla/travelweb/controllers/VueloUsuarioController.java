package com.unla.travelweb.controllers;


import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.unla.travelweb.converters.AerolineaConverter;
import com.unla.travelweb.converters.ClaseConverter;
import com.unla.travelweb.converters.DestinoConverter;
import com.unla.travelweb.converters.ReservaVueloConverter;
import com.unla.travelweb.converters.UsuarioConverter;
import com.unla.travelweb.entities.Aerolinea;
import com.unla.travelweb.entities.User;
import com.unla.travelweb.entities.Usuario;
import com.unla.travelweb.helpers.ViewRouteHelper;
import com.unla.travelweb.models.AerolineaModel;
import com.unla.travelweb.models.ClaseModel;
import com.unla.travelweb.models.DestinoModel;
import com.unla.travelweb.models.Functions;
import com.unla.travelweb.models.ListaUsuariosModel;
import com.unla.travelweb.models.ReservaVueloModel;
import com.unla.travelweb.models.UsuarioModel;
import com.unla.travelweb.models.VueloModel;
import com.unla.travelweb.repositories.IReservaVueloRepository;
import com.unla.travelweb.repositories.IUserRepository;
import com.unla.travelweb.services.IAerolineaService;
import com.unla.travelweb.services.ICalificacionAerolineaService;
import com.unla.travelweb.services.IClaseService;
import com.unla.travelweb.services.IDestinoService;
import com.unla.travelweb.services.IReservaVueloService;
import com.unla.travelweb.services.IUsuarioService;
import com.unla.travelweb.services.IVueloService;

@Controller
@RequestMapping("/vueloUsuario")
public class VueloUsuarioController {
	
	@Autowired
	@Qualifier ("vueloService")
	private IVueloService vueloService;
	
	@Autowired
	@Qualifier ("reservaVueloService")
	private IReservaVueloService reservaVueloService;
	
	@Autowired
	@Qualifier ("claseService")
	private IClaseService claseService;
	
	@Autowired
	@Qualifier ("destinoService")
	private IDestinoService destinoService;
	
	@Autowired
	@Qualifier("aerolineaService")
	private IAerolineaService aerolineaService;
	
	@Autowired
	@Qualifier("calificacionAerolineaService")
	private ICalificacionAerolineaService calificacionAerolineaService;
	
	@Autowired
	@Qualifier("userRepository")
	private IUserRepository userRepository;
	
	@Autowired
	@Qualifier("reservaVueloConverter")
	private ReservaVueloConverter reservaVueloConverter;
	
	@Autowired
	@Qualifier("reservaVueloRepository")
	private IReservaVueloRepository reservaVueloRepository;

	@Autowired
	@Qualifier("destinoConverter")
	private DestinoConverter destinoConverter;
	
	@Autowired
	@Qualifier("aerolineaConverter")
	private AerolineaConverter aerolineaConverter;
	
	@Autowired
	@Qualifier("claseConverter")
	private ClaseConverter claseConverter;
	
	@Autowired
	@Qualifier("usuarioConverter")
	private UsuarioConverter usuarioConverter;
	
	@Autowired
	@Qualifier("usuarioService")
	private IUsuarioService usuarioService;
	
	
	@GetMapping ("")
	public ModelAndView index() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.VUELO_USUARIO);
        mAV.addObject("vuelos", vueloService.getAll());
        return mAV;
    }
	
	@GetMapping ("/vueloReserva/{id}")
	public ModelAndView reservar(@PathVariable("id") long id) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.VUELO_RESERVA);
        mAV.addObject("vuelo", vueloService.findById(id));
        mAV.addObject("clases", claseService.getAll());
        mAV.addObject("aerolineas", aerolineaService.getAll());
        return mAV;
    }
	
	@PostMapping("/create")
    public ModelAndView create(@ModelAttribute("vuelo") VueloModel vueloModel,@AuthenticationPrincipal UserDetails currentUser, Model model) {

		ClaseModel c = claseService.findById(vueloModel.getClase().getId());
		AerolineaModel a = aerolineaService.findById(vueloModel.getAerolinea().getId());
		
		User user = (User) userRepository.findByUsernameAndFetchUserRolesEagerly(currentUser.getUsername());
		
		DestinoModel de = destinoService.findById(vueloModel.getDestino().getId());
		
		DestinoModel or = destinoService.findById(vueloModel.getOrigen().getId());

		double precio = Math.round((vueloService.calcularPrecio(4, or, de, c, vueloModel.getCantPersonas())));
		if(vueloModel.getFechaVuelta()==null) {
			precio/=2;
		}
		
		ReservaVueloModel rv = new ReservaVueloModel(vueloModel.getFechaIda(), vueloModel.getFechaVuelta(), a, c,
				vueloModel.isEscalaIncluida(),vueloModel.getOrigen(), vueloModel.getDestino(), precio,vueloModel.getCantPersonas());

		user.getCarrito().getVuelos().add(reservaVueloConverter.modelToEntity(rv));
        
		
        reservaVueloService.insert(rv);
        
        if(rv.getCantPersonas()>=1) {
            ModelAndView mAV2= new ModelAndView(ViewRouteHelper.VUELO_FORMULARIO);

            ListaUsuariosModel lista = new ListaUsuariosModel(1);

            for(int i = 1 ; i <= rv.getCantPersonas(); i++) {
            	lista.getListaU().add(new UsuarioModel());
            }
            System.out.println("Tamaño de la lista:" + lista.getListaU().size());
            mAV2.addObject("lista", lista);
            
            model.addAttribute("lista", lista);
            
            return mAV2;
        }
        else return new ModelAndView("redirect:/vueloUsuario");
    }
	

	@PostMapping("/formulariosOk")
	public ModelAndView formularios(@ModelAttribute("lista") ListaUsuariosModel lista) {
		
		ReservaVueloModel rv = reservaVueloService.findById(reservaVueloService.getAll().get(reservaVueloService.getAll().size() - 1).getId());

		//agregar usuarios con save/insert y setear lista
		for(int i = 0 ; i < lista.getListaU().size(); i++) {
			//Usuario u = usuarioConverter.modelToEntity(lista.getLista().get(i));
			UsuarioModel us = lista.getListaU().get(i);
			us.setReservaVuelo(reservaVueloService.findById(reservaVueloService.getAll().get(reservaVueloService.getAll().size() - 1).getId()));
			usuarioService.insert(us);
		}
		rv.setListaU(lista.getListaU());
		
		return new ModelAndView("redirect:/vueloUsuario");
	}

    @GetMapping("/vueloCrear")
    public ModelAndView create() {
    	
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.VUELO_VUELOCREAR);
        mAV.addObject("vuelo", new VueloModel());
        mAV.addObject("destinos", destinoService.getAll());
        for(Aerolinea a : aerolineaService.getAll()) {
        	a.setValoracion(Functions.valoracionesXaerolinea(a, calificacionAerolineaService.getAll()));;
        }
        mAV.addObject("aerolineas", aerolineaService.getAll());
        mAV.addObject("clases", claseService.getAll());

        return mAV;
    }

    @PostMapping("/vueloCrearOk")
    public ModelAndView createVuelo(@ModelAttribute("vuelo") VueloModel vueloModel,@AuthenticationPrincipal UserDetails currentUser, Model model) {
    	ClaseModel c = claseService.findById(vueloModel.getClase().getId());
		AerolineaModel a = aerolineaService.findById(vueloModel.getAerolinea().getId());
		DestinoModel de = destinoService.findById(vueloModel.getDestino().getId());
		DestinoModel or = destinoService.findById(vueloModel.getOrigen().getId());
		
		User user = (User) userRepository.findByUsernameAndFetchUserRolesEagerly(currentUser.getUsername());

		double precio = Math.round((vueloService.calcularPrecio(4, or, de, c, vueloModel.getCantPersonas())));
		if(vueloModel.getFechaVuelta()==null) {
			precio/=2;
		}
		
		ReservaVueloModel rv = new ReservaVueloModel(vueloModel.getFechaIda(), vueloModel.getFechaVuelta(), a, c,
				vueloModel.isEscalaIncluida(),or, de, precio,vueloModel.getCantPersonas());

		user.getCarrito().getVuelos().add(reservaVueloConverter.modelToEntity(rv));
        
		
        reservaVueloService.insert(rv);
        
        if(rv.getCantPersonas()>=1) {
            ModelAndView mAV2= new ModelAndView(ViewRouteHelper.VUELO_FORMULARIO);

            ListaUsuariosModel lista = new ListaUsuariosModel(1);

            for(int i = 1 ; i <= rv.getCantPersonas(); i++) {
            	lista.getListaU().add(new UsuarioModel());
            }
            System.out.println(lista.getListaU().size());
            mAV2.addObject("lista", lista);
            
            model.addAttribute("lista", lista);
            
            return mAV2;
        }
        else return new ModelAndView("redirect:/vueloUsuario");
    }
	
	
	
	@InitBinder     
    public void initBinder(WebDataBinder binder){
         binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true, 10));   
    }
	

}