# Tutorial configuração do eclipse para desenvolvimento de plugins

Passo a passo de como instalar e configurar a plataforma eclipse para desenvolver plugins para o Tryd.

## Instalar eclipse
  * Fazer o download do eclipse installer em https://www.eclipse.org/downloads
  * Execute o instalador e escolha um pacote que tenha disponível os componentes PDE, de peferência escolha o 'Eclipse IDE for Enterprise Java and Web Developers'. Tutorial oficial em: https://www.eclipse.org/downloads/packages/installer
Se já tive um eclipse instalado, sua versão provavelmente pode ser usada, não é necessário instalar a última versão.

## Configurações iniciais
  * Abra a plataforma eclipse
  * Ao abrir, selecione um diretório para ser seu workspace.

  ![workspace](workspace.png)

* Se preferir, pode mudar o idioma da interface instalando um language pack: https://www.eclipse.org/babel/downloads.php

## Configurar JVM
  * O desenvolvimento e a execução dos plugins devem ser feitos com a mesma versão JRE que vem junto com o Tryd, versão 1.8
  * Acesse o menu Window - Preferences - Java - Installed JREs
  * Clique em Add e selecione Standard VM
  * Em JRE home selecione o diretório onde estar instalada a JRE do Tryd, geralmente em C:\Tryd5\jre

  ![Tryd JRE](jre.png)
  
## Configurar Target Platform
  * A execução do plugin precisa de uma plataforma eclipse (Tryd) onde serão testados e depurados
  * Acesso o menu Window - Preferences - Plug-in Development - Target Platfom
  * Clique em Add e selecione a opção Nothing
  * Dê um nome pra plataforma (e.g: Tryd 6)
  * Na aba Locations clique em Add e selecione Installation. Na próxima tela clique em Browse e selecione o diretório on o Tryd está instalado, geralmente C:\Tryd5
  * Na aba Content, em 'Manage using' selecione a opção Plug-ins e certifique de que todos os plugins estejam selecionados
  * Na aba Environment defina as veriáveis conforme abaixo:

  ![Environment](platform_env.png)

* Na aba Arguments ente o valor abaixo no campo VM Arguments (ctrl-c ctrl-v):
  
  `-Dosgi.requiredJavaVersion=1.8 -Dosgi.instance.area.default=@user.home/eclipse-workspace -XX:+UseG1GC -XX:+UseStringDeduplication --add-modules=ALL-SYSTEM -Dosgi.requiredJavaVersion=1.8 -Dosgi.dataAreaRequiresExplicitInit=true -Xms256m -Xmx1024m --add-modules=ALL-SYSTEM -Declipse.p2.max.threads=10 -Doomph.update.url=http://download.eclipse.org/oomph/updates/milestone/latest -Doomph.redirection.index.redirection=index:/->http://git.eclipse.org/c/oomph/org.eclipse.oomph.git/plain/setups/`
  
## Criar projeto de plug-in
 * Crie um projeto do tipo Plug-in acessando o menu File - New - Project...
 * Selecione o wizard 'Plug-in Project'
 * Dê um nome ao projeto e faça as seleções conforme abaixo:

 ![Plug-in project](project_wizard_1.png)

 * Preencha as propriedades conforme necessário. Selecionar JavaSE-1.8 em Execution Environment.

 ![Content](project_wizard_2.png)
 
 * Não selecione nenhum template na janela Templates

## Importar indicador de exemplo
 * Se preferir, você pode importar o projeto de exemplo disponível em: 
