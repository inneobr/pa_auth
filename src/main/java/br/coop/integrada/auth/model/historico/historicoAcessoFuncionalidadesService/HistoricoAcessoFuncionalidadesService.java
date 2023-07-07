package br.coop.integrada.auth.model.historico.historicoAcessoFuncionalidadesService;

import java.util.Date;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.coop.integrada.auth.AuthApplication;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor @Transactional
public class HistoricoAcessoFuncionalidadesService {
	private static final Logger logger = LoggerFactory.getLogger(AuthApplication.class);
	
	@Autowired
	private TodoHistoricoAcessoFuncionalidades todoHistorico;
	
	public HistoricoAcessoFuncionalidades salvarHistorico(Long idUsuario, StackTraceElement[] stackTraceElements) {
		StackTraceElement ste = stackTraceElements[1];	
		String operacao = ste.getClassName()+" Method: "+ste.getMethodName();
		
		logger.info("Historico de acesso salvo.");
		return todoHistorico.save(new HistoricoAcessoFuncionalidades(null, idUsuario,  operacao, new Date() ));
	}

}
