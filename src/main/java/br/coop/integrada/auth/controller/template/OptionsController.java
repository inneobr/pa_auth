package br.coop.integrada.auth.controller.template;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.coop.integrada.auth.model.theme.Options;
import br.coop.integrada.auth.modelDto.menu.MenuSimplesDto;
import br.coop.integrada.auth.service.OptionsService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/agricola/auth/v1/template")
@SecurityRequirement(name = "javainuseapi")
@Tag(name = "Opções do template", description = "Carrega as configurações personalizadas do template.")
public class OptionsController {
	
	@Autowired
	private OptionsService optionsService;
	
	@PostMapping
	public ResponseEntity<String> salvarOptions(@RequestBody Options options) {
		try {
			optionsService.salvarOptions(options);
			return ResponseEntity.ok().body("Definições de template salvas com sucesso!");		
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel salvar as definições de template!");
		}
	}
	
	@GetMapping("/{idUsuario}")
	public ResponseEntity<Options> buscarOptions(@PathVariable(name="idUsuario") Long idUsuario) {
		return new ResponseEntity<Options>(optionsService.buscarOptions(idUsuario), HttpStatus.ACCEPTED);

	}
	
	@GetMapping("listar-menus/{idUsuario}")
	public ResponseEntity<?> listarMenuUsuario(@PathVariable(name="idUsuario") Long idUsuario){
		List<MenuSimplesDto> menuSimplesDtos = optionsService.buscarMenus(idUsuario);
		return ResponseEntity.ok(menuSimplesDtos);
	}

}
