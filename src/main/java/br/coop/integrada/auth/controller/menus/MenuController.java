package br.coop.integrada.auth.controller.menus;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.coop.integrada.auth.model.menu.Menu;
import br.coop.integrada.auth.modelDto.menu.MenuDto;
import br.coop.integrada.auth.service.MenuService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/agricola/auth/v1/menu")
@SecurityRequirement(name = "javainuseapi")
@Tag(name = "Menus", description = "Menus de usúario.")
public class MenuController {
	
	@Autowired
	private MenuService menuService;
	
	@PostMapping
	public ResponseEntity<String> cadastrarMenu(@RequestBody @Valid MenuDto menuDto){
		try {
			menuService.cadastrarMenu(menuDto);
			return ResponseEntity.ok().body("Menu cadastrado!");		
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel cadastrar o menu!");
		}	
	}
	
	@PutMapping
	public ResponseEntity<String> atualizarMenu(@RequestBody @Valid MenuDto menuDto){
		try {
			menuService.atualizarMenu(menuDto);
			return ResponseEntity.ok().body("Menu atualizado!");		
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel atualizar!");
		}		
	}
	
	@GetMapping
	public ResponseEntity<List<Menu>> listarMenu(){
		return new ResponseEntity<List<Menu>>(menuService.listarMenu() , HttpStatus.ACCEPTED);		
	}
	
	@GetMapping("/id/{idMenu}")
	public ResponseEntity<Menu> unicoMenu(@PathVariable(name="idMenu") Long idMenu){	
		return new ResponseEntity<Menu>(menuService.unicoMenu(idMenu) , HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/inativos")
	public ResponseEntity<List<Menu>> listarMenuInativo(){
		return new ResponseEntity<List<Menu>>(menuService.listarMenuInativo() , HttpStatus.ACCEPTED);		
	}
	
	@PutMapping("/ativar/{idMenu}")
	public ResponseEntity<String> ativarMenu(@PathVariable(name="idMenu") Long idMenu){	
		try {
			menuService.inativarMenu(idMenu);
			return ResponseEntity.ok().body("Menu inativado!");		
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel inativar o menu!");
		}
	}
	
	@PutMapping("/inativar/{idMenu}")
	public ResponseEntity<String> inativarMenu(@PathVariable(name="idMenu") Long idMenu){	
		try {
			menuService.ativarMenu(idMenu);
			return ResponseEntity.ok().body("Menu ativado!");		
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel ativar o menu!");
		}
	}
	
	

}
