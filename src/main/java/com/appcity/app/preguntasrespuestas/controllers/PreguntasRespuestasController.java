package com.appcity.app.preguntasrespuestas.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.appcity.app.preguntasrespuestas.clients.EstadisticaFeignClient;
import com.appcity.app.preguntasrespuestas.clients.SubscripcionesFeignClient;
import com.appcity.app.preguntasrespuestas.models.PreguntasRespuestas;
import com.appcity.app.preguntasrespuestas.repository.PreguntasRespuestasRepository;
import com.appcity.app.preguntasrespuestas.request.Preguntas;
import com.appcity.app.preguntasrespuestas.request.Respuestas;

@RestController
public class PreguntasRespuestasController {

	@Autowired
	PreguntasRespuestasRepository preguntasRespuestas;

	@Autowired
	EstadisticaFeignClient estadistica;

	@Autowired
	SubscripcionesFeignClient subs;

	@PutMapping("/preguntasrespuestas/crearpreguntas/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public String editarPreguntas(@PathVariable("nombre") String nombre, @RequestBody Preguntas preguntas) {
		try {
			PreguntasRespuestas proyecto = preguntasRespuestas.findByNombre(nombre);
			List<Preguntas> pregunta = new ArrayList<Preguntas>();
			List<List<String>> respuesta = new ArrayList<List<String>>();
			List<List<List<String>>> respuestaTotal = proyecto.getRespuestas();
			List<List<Preguntas>> preguntasTotal = proyecto.getPreguntas();
			List<List<List<String>>> respuestaUsuarios = proyecto.getRespuestasUsuario();
			if (preguntas.getPriorizacion() == null) {
				preguntas.setPriorizacion("impacto");
			}
			preguntas.setNumeroPregunta(proyecto.getPreguntas().size() + 1);
			if (preguntas.getTipoConsulta() == 1 || preguntas.getTipoConsulta() == 2 || preguntas.getTipoConsulta() == 3
					|| preguntas.getTipoConsulta() == 4) {
				if (preguntas.getTipoConsulta() == 2 || preguntas.getTipoConsulta() == 34) {
					preguntas.setPriorizacion(null);
				}
				if (preguntas.getOpciones() == null) {
					return "Es obligatorio poner las opciones";
				} else {
					pregunta.add(preguntas);
					respuestaTotal.add(respuesta);
					respuestaUsuarios.add(respuesta);
					preguntasTotal.add(pregunta);
					proyecto.setRespuestas(respuestaTotal);
					proyecto.setRespuestas(respuestaUsuarios);
					proyecto.setPreguntas(preguntasTotal);
					preguntasRespuestas.save(proyecto);
					return "Pregunta almacenada correctamente en el proyecto: " + nombre;
				}
			} else {
				preguntas.setPriorizacion(null);
				preguntas.setOpciones(null);
				pregunta.add(preguntas);
				respuestaTotal.add(respuesta);
				respuestaUsuarios.add(respuesta);
				preguntasTotal.add(pregunta);
				proyecto.setPreguntas(preguntasTotal);
				proyecto.setRespuestas(respuestaUsuarios);
				proyecto.setRespuestas(respuestaTotal);
				preguntasRespuestas.save(proyecto);
				return "Pregunta almacenada correctamente en el proyecto: " + nombre;
			}

		} catch (Exception e) {
			return "Proyecto: " + nombre + " no existe, error: " + e.getMessage();
		}
	}

	@PostMapping("/preguntasrespuestas/crear")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void crearProyecto(@RequestParam("nombre") String nombre) {
		PreguntasRespuestas proyectos = new PreguntasRespuestas();
		proyectos.setRespuestas(new ArrayList<List<List<String>>>());
		proyectos.setNombre(nombre);
		proyectos.setPreguntas(new ArrayList<List<Preguntas>>());
		proyectos.setUsuarios(new ArrayList<String>());
		proyectos.setRespuestasUsuario(new ArrayList<List<List<String>>>());
		preguntasRespuestas.save(proyectos);
	}

	@PutMapping("/preguntasrespuestas/respuestas/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public String editarRespuesta(@PathVariable("nombre") String nombre, @RequestBody Respuestas preguntas) {
		try {
			PreguntasRespuestas proyecto = preguntasRespuestas.findByNombre(nombre);
			List<List<List<String>>> listaRespuestas = proyecto.getRespuestas();
			for (int i = 0; i < listaRespuestas.size(); i++) {
				listaRespuestas.get(i).add(preguntas.getRespuesta().get(i));
			}
			proyecto.setRespuestas(listaRespuestas);
			preguntasRespuestas.save(proyecto);
			subs.inscribirCuestionario(nombre, preguntas.getUsername());
			estadistica.obtenerEstadistica(nombre);
			return "Respuesta añadida";
		} catch (Exception e) {
			return "Proyecto: " + nombre + " no existe, error: " + e.getMessage() + ":" + e.getLocalizedMessage();
		}

	}

	@GetMapping("/preguntasrespuestas/obtenerProyectoByNombre/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public PreguntasRespuestas getProyectosByNombre(@PathVariable("nombre") String nombre) {
		return preguntasRespuestas.findByNombre(nombre);
	}

	@GetMapping("/preguntasrespuestas/verPreguntas/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<List<Preguntas>> verPreguntas(@PathVariable("nombre") String nombre) {
		PreguntasRespuestas proyecto = preguntasRespuestas.findByNombre(nombre);
		if (proyecto.getPreguntas().size() != 0) {
			return proyecto.getPreguntas();
		} else {
			return null;
		}

	}

	@GetMapping("/preguntasrespuestas/verRespuestas/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<List<List<String>>> verRespuestas(@PathVariable("nombre") String nombre) {
		PreguntasRespuestas proyecto = preguntasRespuestas.findByNombre(nombre);
		return proyecto.getRespuestas();
	}

	@DeleteMapping("/preguntasrespuestas/borrarPreguntas/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void borrarPreguntas(@PathVariable String nombre) {
		PreguntasRespuestas proyecto = preguntasRespuestas.findByNombre(nombre);
		String id = proyecto.getId();
		preguntasRespuestas.deleteById(id);
	}

	@PutMapping("/preguntasrespuestas/abrirCuestionario/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void abrirCuestionario(@PathVariable("nombre") String nombre, @RequestBody Respuestas preguntas) {
		PreguntasRespuestas proyecto = preguntasRespuestas.findByNombre(nombre);
		List<String> personas = proyecto.getUsuarios();
		if(!personas.contains(preguntas.getUsername())) {
			personas.add(preguntas.getUsername());
			List<List<List<String>>> listaRespuestas = proyecto.getRespuestasUsuario();
			for (int i = 0; i < listaRespuestas.size(); i++) {
				listaRespuestas.get(i).add(new ArrayList<String>());
			}
			proyecto.setUsuarios(personas);
			proyecto.setRespuestasUsuario(listaRespuestas);
			preguntasRespuestas.save(proyecto);
		}
	}

	@PutMapping("/preguntasrespuestas/respuestasPregunta/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void respuestasPregunta(@PathVariable("nombre") String nombre, @RequestBody Respuestas preguntas) {
		PreguntasRespuestas proyecto = preguntasRespuestas.findByNombre(nombre);
		List<List<Preguntas>> preguntasTotal = proyecto.getPreguntas();
		Integer valueUser = proyecto.getUsuarios().indexOf(preguntas.getUsername());
		List<List<List<String>>> respuestaUsuarios = proyecto.getRespuestasUsuario();
		List<List<String>> respuestaUsuario = respuestaUsuarios.get(preguntas.getNumeroPregunta() - 1);
		System.out.println(respuestaUsuario + "<----------");
		List<String> opcionesUsuario = preguntas.getRespuestaUsuario();
		List<String> respuesta = new ArrayList<String>();
		List<String> opciones = preguntasTotal.get(preguntas.getNumeroPregunta() - 1).get(0).getOpciones();
		System.out.println(opciones);
		if (preguntasTotal.get(preguntas.getNumeroPregunta() - 1).get(0).getTipoConsulta() == 1) {
			System.out.println("AQUI1:");
			Collections.reverse(opcionesUsuario);
			System.out.println(opcionesUsuario);
			Double value = 100.0 / opciones.size();
			for (int i = 0; i < opciones.size(); i++) {
				Double index = (double) (opcionesUsuario.indexOf(opciones.get(i)) + 1);
				System.out.println("index Of: -->" + index + "<---- INDEX");
				respuesta.add(String.valueOf(value * index));
				System.out.println("Value: -->" + value);
			}
			respuestaUsuario.set(valueUser, respuesta);
		} else {
			System.out.println("AQUI:2");
			respuestaUsuario.set(valueUser, preguntas.getRespuestaUsuario());
		}

		respuestaUsuarios.set(preguntas.getNumeroPregunta() - 1, respuestaUsuario);
		proyecto.setRespuestasUsuario(respuestaUsuarios);
		preguntasRespuestas.save(proyecto);
	}

	@PutMapping("/preguntasrespuestas/respuestaFinal/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public String respuestaFinal(@PathVariable("nombre") String nombre, @RequestBody Respuestas preguntas) {
		try {
			PreguntasRespuestas proyecto = preguntasRespuestas.findByNombre(nombre);
			List<List<List<String>>> listaRespuestas = proyecto.getRespuestas();
			List<List<List<String>>> listaRespuestasUsuario = proyecto.getRespuestasUsuario();
			Integer index = proyecto.getUsuarios().indexOf(preguntas.getUsername());
			for (int i = 0; i < listaRespuestasUsuario.size(); i++) {
				if (listaRespuestasUsuario.get(i).get(index).size() == 0) {
					return "LLenar toddas las opciones";
				} else {
					listaRespuestas.get(i).add(listaRespuestasUsuario.get(i).get(index));
				}
			}

			List<String> usuarios = proyecto.getUsuarios();
			for (int i = 0; i < listaRespuestasUsuario.size(); i++) {
				List<List<String>> res = listaRespuestasUsuario.get(i);
				res.remove(proyecto.getUsuarios().indexOf(preguntas.getUsername()));
				listaRespuestasUsuario.set(i, res);
			}
			usuarios.remove(preguntas.getUsername());
			proyecto.setRespuestas(listaRespuestas);
			proyecto.setRespuestasUsuario(listaRespuestasUsuario);
			preguntasRespuestas.save(proyecto);
			subs.inscribirCuestionario(nombre, preguntas.getUsername());
			estadistica.obtenerEstadistica(nombre);
			return "Respuesta añadida";
		} catch (Exception e) {
			return "Proyecto: " + nombre + " no existe, error: " + e.getMessage() + ":" + e.getLocalizedMessage();
		}

	}

}
