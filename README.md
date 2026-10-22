# Inventário TI

Sistema Java Web para cadastro e consulta de equipamentos de TI.

## Stack inicial
- Java 25, Maven e Spring Boot 3.5.16
- Spring MVC + Thymeleaf
- MySQL + Spring Data JPA
- Empacotamento WAR para avaliar implantação em Tomcat externo

## Atenção à hospedagem
A geração do WAR não garante que a hospedagem compartilhada HostGator execute Java/Tomcat. A documentação atual da HostGator indica Java/Tomcat para VPS ou servidores dedicados, não para planos compartilhados. Confirme o plano antes de planejar a publicação.

## Estrutura planejada
- `controller/`: rotas HTTP
- `service/`: regras de negócio
- `repository/`: acesso ao banco
- `entity/`: entidades persistidas
- `templates/`: páginas Thymeleaf

## Próximos passos
1. Criar banco MySQL local de desenvolvimento.
2. Definir regras para Número, NRP, MAC, IP e status.
3. Implementar entidade, repositório, serviço e telas CRUD.
4. Implementar filtros por localidade, prefixo de IP, Número e NRP.
5. Exportar CSV respeitando os filtros.
6. Implementar autenticação, permissões, auditoria e backups antes da implantação.

Nunca coloque credenciais reais neste repositório. O sistema deve ser publicado somente com HTTPS, autenticação e estratégia de backup.
