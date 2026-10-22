# Arquitetura inicial — Inventário TI

## Abordagem
Aplicação monolítica modular, mais simples de aprender e operar do que microserviços.

## Camadas
1. Controller: recebe requisições web.
2. Service: regras de negócio e validações.
3. Repository: operações de persistência.
4. Entity: mapeamento relacional.
5. Templates: interface HTML com Thymeleaf.

## Campos previstos para Equipamento
- id interno
- número
- NRP
- localidade
- MAC address
- IP
- servidor
- número do switch
- porta do switch
- status
- criadoEm / atualizadoEm

## Decisões pendentes antes do CRUD
- Número e NRP são únicos? Podem ficar vazios?
- MAC e IP precisam ser únicos?
- Quais valores de status serão aceitos?
- Localidade e servidor serão texto livre ou tabelas próprias?
- Busca de prefixo IP: por exemplo, `10.20` corresponde a endereços que começam por `10.20.`.
- Exclusão física ou inativação lógica?
- Quais perfis de usuário e trilha de auditoria são necessários?

## Implantação
Confirmar suporte a Java persistente, Tomcat, versão do Java, deploy WAR, recursos e conectividade com MySQL. Se Java não for permitido no plano compartilhado, hospedar a aplicação em serviço compatível e proteger a comunicação com o banco por rede privada ou regras de acesso suportadas pelo provedor.
