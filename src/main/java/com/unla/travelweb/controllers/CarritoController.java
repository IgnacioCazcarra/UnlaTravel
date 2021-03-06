package com.unla.travelweb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.ArrayList;

import com.unla.travelweb.converters.HotelConverter;
import com.unla.travelweb.converters.ReservaHotelConverter;
import com.unla.travelweb.entities.Hotel;
import com.unla.travelweb.entities.ReservaHotel;
import com.unla.travelweb.entities.User;
import com.unla.travelweb.helpers.ViewRouteHelper;
import com.unla.travelweb.models.CarritoModel;

import com.unla.travelweb.models.UsuarioModel;
import com.unla.travelweb.models.HotelModel;
import com.unla.travelweb.repositories.IUserRepository;
import com.unla.travelweb.services.ICarritoService;
import com.unla.travelweb.services.IUsuarioService;

import com.unla.travelweb.services.IReservaVueloService;
import com.unla.travelweb.services.IHotelService;
import com.unla.travelweb.services.IReservaActividadService;
import com.unla.travelweb.services.IReservaHotelService;

@Controller
@RequestMapping("/carrito")
public class CarritoController {
	@Autowired
	@Qualifier ("carritoService")
	private ICarritoService carritoService;
	
	@Autowired
	@Qualifier("userRepository")
	private IUserRepository userRepository;
	@Autowired
	@Qualifier ("hotelService")
	private IHotelService hotelService;
	@Autowired
	@Qualifier ("hotelConverter")
	private HotelConverter hotelConverter;
	
	@Autowired
	@Qualifier ("usuarioService")
	private IUsuarioService usuarioService;
	
	@Autowired
	@Qualifier ("reservaVueloService")
	private IReservaVueloService reservaVueloService;
	
	@Autowired
	@Qualifier ("reservaHotelService")
	private IReservaHotelService reservaHotelService;
	
	@Autowired
	@Qualifier ("reservaActividadService")
	private IReservaActividadService reservaActividadService;
	
	
	@Autowired
	@Qualifier ("reservaHotelConverter")
	private ReservaHotelConverter reservaHotelConverter;
	
	
//	@GetMapping ("")
//	public ModelAndView index() {
//        ModelAndView mAV = new ModelAndView(ViewRouteHelper.CARRITO_INDEX);
//        mAV.addObject("carritos", carritoService.getAll());
//        return mAV;
//    }
	
	@RequestMapping ("")
	public ModelAndView index(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.CARRITO_INDEX);
        User user = (User) userRepository.findByUsernameAndFetchUserRolesEagerly(currentUser.getUsername());
        model.addAttribute("currentUser", user);
        model.addAttribute("hoteles", user.getCarrito().getHoteles());
        model.addAttribute("actividades", user.getCarrito().getActividades());
        model.addAttribute("vuelos", user.getCarrito().getVuelos());

        return mAV;
    }
	
	@GetMapping("/{id}")
    public ModelAndView get(@PathVariable("id") long id) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.CARRITO_UPDATE);
        mAV.addObject("carrito", carritoService.findById(id));
        return mAV;
    }
	
	@PostMapping("/update")
    public RedirectView update(@ModelAttribute("carrito") CarritoModel carritoModel) {
        carritoService.update(carritoModel);
        return new RedirectView(ViewRouteHelper.CARRITO_ROOT);
    }
	
	@PostMapping("/delete/{id}")
    public RedirectView delete(@PathVariable("id") long id) {
        carritoService.remove(id);
        return new RedirectView(ViewRouteHelper.CARRITO_ROOT);
    }
	
	@PostMapping("/deleteHotel/{id}/{index}")
    public RedirectView deleteHotel(@PathVariable("id") long id, @PathVariable("index") int index, @AuthenticationPrincipal UserDetails currentUser) {
		User user = (User) userRepository.findByUsernameAndFetchUserRolesEagerly(currentUser.getUsername());
        user.getCarrito().getHoteles().remove(index);
		reservaHotelService.remove(id);
		reservaHotelService.remove(id-1);
        return new RedirectView(ViewRouteHelper.CARRITO_ROOT);
    }
	
	@PostMapping("/deleteActividad/{id}/{index}")
    public RedirectView deleteActividad(@PathVariable("id") long id, @PathVariable("index") int index, @AuthenticationPrincipal UserDetails currentUser) {
		User user = (User) userRepository.findByUsernameAndFetchUserRolesEagerly(currentUser.getUsername());
        user.getCarrito().getActividades().remove(index);
		reservaActividadService.remove(id);
		reservaActividadService.remove(id-1);
        return new RedirectView(ViewRouteHelper.CARRITO_ROOT);
    }
	
	@PostMapping("/deleteVuelo/{id}/{index}")
    public RedirectView deleteVuelo(@PathVariable("id") long id, @PathVariable("index") int index, @AuthenticationPrincipal UserDetails currentUser) {
		User user = (User) userRepository.findByUsernameAndFetchUserRolesEagerly(currentUser.getUsername());
		
		for(int i=usuarioService.getAll().size()-1; i>=0; i--) {
			if(usuarioService.getAll().get(i).getReservaVuelo().getId() == reservaVueloService.findById(id).getId()) {
				usuarioService.remove(usuarioService.getAll().get(i).getId());
			}
		}
		
		user.getCarrito().getVuelos().remove(index);        
		reservaVueloService.remove(id);
		reservaVueloService.remove(id-1);
        return new RedirectView(ViewRouteHelper.CARRITO_ROOT);
    }
	
	@GetMapping ("/infoVuelo/{id}")
	public ModelAndView infoVuelo(@PathVariable("id") long id) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.CARRITO_VUELO);
        if(id%2==1) {
        	id+=1;
        }
        mAV.addObject("vuelo", reservaVueloService.findById(id));
        
        List<UsuarioModel> usuarios = new ArrayList<UsuarioModel>();
        int i = 1;
        while ((i <= usuarioService.getAll().size()) || (usuarios.size() != reservaVueloService.findById(id).getCantPersonas())) {
        	
	        	if (usuarioService.findById(i).getReservaVuelo().getId() == id) {
	        		usuarios.add(usuarioService.findById(i));
	        	}
        i++;
        }
        
        /*for (i=1;i<=usuarioService.getAll().size(); i++) {
        	if (usuarioService.findById(i).getReservaVuelo().getId() == id) {
        		usuarios.add(usuarioService.findById(i));
        	}
        }*/
        mAV.addObject("usuarios", usuarios);
        
        return mAV;
    }

}
