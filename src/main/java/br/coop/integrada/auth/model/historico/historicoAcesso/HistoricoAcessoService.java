package br.coop.integrada.auth.model.historico.historicoAcesso;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor @Transactional
public class HistoricoAcessoService {
	
	@Autowired
	private TodoHistoricoAcesso todoHistorico;
	
	@Autowired 
	private HttpServletRequest request;
	
	public void salvarHistorico(Long idUsuario) {
		
		String ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null) {
		    ipAddress = request.getHeader("X_FORWARDED_FOR");
		    if (ipAddress == null){
		        ipAddress = request.getRemoteAddr();
		    }
		}		
		todoHistorico.save(new HistoricoAcesso(null, idUsuario, ipAddress, new Date()));
	}


}
