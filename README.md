# tryd-plugins

Indicadores para a plataforma de negociação Tryd

# Termos de uso

**Os plugins desenvolvidos por mim tem a finalizade de auxiliar a comunidade de traders do Brasil. Os plugins foram testados antes de serem disponibilizados, porém não me responsabilizo por algum problema que possa ocorrer, ao utilizar qualquer plugin você concorda que o autor não tem nenhuma responsabilidade por danos causados.**

O código fonte dos plugins é aberto, fique a vontade para contribuir, reportar erros e dar sugestões de melhoria.

# Instalação

## Antes de instalar faça backup

Realize um backup completo da pasta onde o Tryd está instalado, geralmente C:\Tryd5

Compacte a pasta em um arquivo zip e mova para um outro local.

Este procedimento é necessário para que a plataforma seja restaurada com rapidez caso haja algum bug nos indicadores instalados que impeçam a inicialização do Tryd.

## Procedimento

1. Com a plataforma Tryd aberta, vá no menu Ajuda -> Instalar Novo Programa
2. Ao lado do combo "Instalar de:", clice no botão "Adicionar..."
3. Na janela "Adicionar Repositório" preencha os campos:

   Nome: KAN Tryd Plugins
   
   Localização: https://trydplugins.herokuapp.com/kan-update-site

4. Clique OK
5. No combo "Instalar de:", selecione "KAN Tryd Plugins"
6. No quadro abaixo, clique na seta à esquerda de "Tryd plugins" para mostrar os plugins disponíveis
7. Selecione os plugins que deseja instalar e clique em Avançar
8. Prossiga clicando no botão Avançar e aceite os termos de licença quando solicitado

# Desinstalação

1. Acesse o menu Ajuda -> Sobre o Tryd
2. Clique em "Detalhes da Instalaçao"
3. Selecione os plugins de deseja desinstalar
4. Clique no botão "Desinstalar"

# Como usar os indicadores

## Variação do VTC

Esse indicador só funciona para os ativos de dolar DOL e WDO.

Para colocá-lo no gráfico, acesse Novo Indicador -> Dados de Mercado -> Variação do VTC

Configure de acrodo com suas preferências.

Se a opção "Obter Valor do VTC das notícias" estiver marcada, o indicador será mostrado no gráfico assim que chegar a notícia do call do VTC.

Para o indicador funcionar no replay, desmarque a opção "Obter Valor do VTC das notícias" e digite o valor no campo "Valor do VTC".

## Variação da VWAP

Para colocá-lo no gráfico, acesse Novo Indicador -> Volume -> Variação da VWAP

## Volatilidade da PTAX

A estratégia que usa a vol da PTAX foi repassada pelo mentor e trader Leo Nonato (https://linktr.ee/leononatotrader)

Para colocá-lo no gráfico, acesse Novo Indicador -> Dados de Mercado -> Volatilidade da PTAX


## Removendo os indicadores do gráfico

Para remover qualquer indicador do gráfico caso ele não esteja visível, acesse o menu do gráfico com o botão direito do mouse e vá em Mais -> Mudar Visibilidade de Objetos. Selecionde o indicador que deseja remover e pressione a tecla Delete.
