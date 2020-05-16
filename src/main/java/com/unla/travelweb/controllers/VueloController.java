package com.unla.travelweb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.unla.travelweb.helpers.ViewRouteHelper;
import com.unla.travelweb.models.VueloModel;
import com.unla.travelweb.services.IVueloService;

@Controller
@RequestMapping("/vuelo")
public class VueloController {
	
	@Autowired
	@Qualifier("vueloService")
	private IVueloService vueloService;

	@GetMapping("")
    public ModelAndView index() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.VUELO_INDEX);
        mAV.addObject("vuelos", vueloService.getAll());
        return mAV;
    }

    @GetMapping("/new")
    public ModelAndView create() {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.VUELO_NEW);
        mAV.addObject("vuelo", new VueloModel());
        return mAV;
    }

    @PostMapping("/create")
    public RedirectView create(@ModelAttribute("vuelo") VueloModel vueloModel) {
    	vueloService.insert(vueloModel);
        return new RedirectView(ViewRouteHelper.VUELO_ROOT);
    }

    @GetMapping("/{id}")
    public ModelAndView get(@PathVariable("id") long id) {
        ModelAndView mAV = new ModelAndView(ViewRouteHelper.VUELO_UPDATE);
        mAV.addObject("vuelo", vueloService.findById(id));
        return mAV;
    }


    @PostMapping("/update")
    public RedirectView update(@ModelAttribute("vuelo") VueloModel vueloModel) {
    	vueloService.update(vueloModel);
        return new RedirectView(ViewRouteHelper.VUELO_ROOT);
    }

    @PostMapping("/delete/{id}")
    public RedirectView delete(@PathVariable("id") long id) {
    	vueloService.remove(id);
        return new RedirectView(ViewRouteHelper.VUELO_ROOT);
    }

}