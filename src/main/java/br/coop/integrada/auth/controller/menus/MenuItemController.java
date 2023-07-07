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

import br.coop.integrada.auth.exception.DefaultExceptions;
import br.coop.integrada.auth.model.item.Item;
import br.coop.integrada.auth.modelDto.item.ItemDto;
import br.coop.integrada.auth.service.ItemService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/agricola/auth/v1/menuItem")
@SecurityRequirement(name = "javainuseapi")
@Tag(name = "MenuItems", description = "Itens do menu do usuário.")
public class MenuItemController {
	
	@Autowired
	private ItemService menuItemService;
	
	@PostMapping
	public ResponseEntity<Item> cadastrarMenu(@RequestBody @Valid ItemDto ItemDto){
		return new ResponseEntity<Item> (menuItemService.cadastrarItem(ItemDto), HttpStatus.CREATED);		
	}
	
	@PutMapping
	public ResponseEntity<Item> atualizarMenu(@RequestBody @Valid ItemDto ItemDto){
		return new ResponseEntity<Item> (menuItemService.atualizarItem(ItemDto), HttpStatus.CREATED);		
	}
	
	@GetMapping
	public ResponseEntity<List<Item>> listarItemMenu(){
		return new ResponseEntity<List<Item>>(menuItemService.listarItemMenu() , HttpStatus.ACCEPTED);		
	}
	
	@GetMapping("/inativos")
	public ResponseEntity<List<Item>> listarItemMenuInativo(){
		return new ResponseEntity<List<Item>>(menuItemService.listarItemMenuInativo() , HttpStatus.ACCEPTED);		
	}
	
	@PutMapping("/inativar/{idMenu}")
	public ResponseEntity<String> inativarItem(@PathVariable(name="idMenu") Long idMenu){	
		try {
			menuItemService.inativarItemMenu(idMenu);
			return ResponseEntity.ok().body("Item inativado com sucesso");		
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel inativar item!");
		}
		
	}
	
	@PutMapping("/ativar/{idMenu}")
	public ResponseEntity<String> ativarItem(@PathVariable(name="idMenu") Long idMenu){	
		try {
			menuItemService.ativarItemMenu(idMenu);
			return ResponseEntity.ok().body("Item ativado com sucesso");		
		}catch(Exception e) {
			return ResponseEntity.badRequest().body("Não foi possivel ativar o item!");
		}
		
	}
	
	@GetMapping("/id/{idItem}")
	public ResponseEntity<?> buscarMenu(@PathVariable(name="idItem") Long idItem){	
		try {
			Item item = menuItemService.buscarItem(idItem);
			return ResponseEntity.ok().body(item);		
		}catch(Exception e) {			
			return ResponseEntity.badRequest().body(DefaultExceptions.construir(HttpStatus.BAD_REQUEST.value(), "Não foi possivel localizar o menu!"));
		}
		
	}

}
