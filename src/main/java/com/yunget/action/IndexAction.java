package com.yunget.action;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "")
public class IndexAction {
	
	@Resource
	private RepositoryService repositoryService;
	
	@RequestMapping(value = "")
	public String index1(){
		return "redirect:/index";
	}
	
	@RequestMapping(value = "index")
	public String index(Model model){
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
		
		model.addAttribute("ilist", list);
		return "index";
	}
	
	@RequestMapping(value="showXml")
	public void showXml(@RequestParam String id, HttpServletResponse reponse) throws IOException {
		ProcessDefinitionQuery pdq = repositoryService.createProcessDefinitionQuery();
		ProcessDefinition pd = pdq.processDefinitionId(id).singleResult();
		
		InputStream is = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getResourceName());
		byte[] b = new byte[1024];
		int len = -1;
		while((len = is.read(b, 0, 1024)) != -1) {
			reponse.getOutputStream().write(b, 0, len);
		}
	}
	
	@RequestMapping(value="showPic")
	public void showPic(@RequestParam String id, HttpServletResponse reponse) throws IOException {
		ProcessDefinitionQuery pdq = repositoryService.createProcessDefinitionQuery();
		ProcessDefinition pd = pdq.processDefinitionId(id).singleResult();
		
		InputStream is = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getDiagramResourceName());
		byte[] b = new byte[1024];
		int len = -1;
		while((len = is.read(b, 0, 1024)) != -1) {
			reponse.getOutputStream().write(b, 0, len);
		}
	}
	
	@RequestMapping(value="delDefinition")
	public String delDefinition(@RequestParam String depId){
		repositoryService.deleteDeployment(depId, true);
		return "redirect:/index";
	}
	
	@RequestMapping(value="deply")
	public String deply(MultipartFile file) throws IOException{
		if(!file.isEmpty()) {
			InputStream is = file.getInputStream();
			repositoryService.createDeployment().addZipInputStream(new ZipInputStream(is)).deploy();
		}
		return "redirect:/index";
	}
}
