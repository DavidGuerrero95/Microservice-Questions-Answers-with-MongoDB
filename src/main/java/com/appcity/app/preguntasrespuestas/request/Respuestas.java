package com.appcity.app.preguntasrespuestas.request;

import java.util.List;

public class Respuestas {

	private String username;

	private List<List<String>> respuesta;

	private List<String> respuestaUsuario;

	private Integer numeroPregunta;

	public Respuestas() {
	}

	public Respuestas(String username, List<List<String>> respuesta, List<String> respuestaUsuario,
			Integer numeroPregunta) {
		super();
		this.username = username;
		this.respuesta = respuesta;
		this.respuestaUsuario = respuestaUsuario;
		this.numeroPregunta = numeroPregunta;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<List<String>> getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(List<List<String>> respuesta) {
		this.respuesta = respuesta;
	}

	public List<String> getRespuestaUsuario() {
		return respuestaUsuario;
	}

	public void setRespuestaUsuario(List<String> respuestaUsuario) {
		this.respuestaUsuario = respuestaUsuario;
	}

	public Integer getNumeroPregunta() {
		return numeroPregunta;
	}

	public void setNumeroPregunta(Integer numeroPregunta) {
		this.numeroPregunta = numeroPregunta;
	}

}
