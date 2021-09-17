package com.appcity.app.preguntasrespuestas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-subscripciones")
public interface SubscripcionesFeignClient {

	@PutMapping("/subscripciones/inscribirCuestionario/{nombre}")
	public void inscribirCuestionario(@PathVariable String nombre, @RequestParam String usuario);
	
}
