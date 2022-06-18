# trabalho-01-INE5680

## Como executar o programa: 

- Primeiramente execute o servidor
- Em seguida execute o cliente

## Passos que devem ser implementados:

- [x] 1. Cliente - usuário entra com login e senha
- [x] 2. Cliente - PBKDF2 – deriva token de autenticação
- [x] 3. Cliente - envia nome do usuário, token e horário para servidor
4, 5, 6, 7. Servidor – Token de autenticação é derivado de novo com Scrypt, compara com valor
guardado no arquivo e valida
- [x] 8. Servidor – gera código TOTP e envia o QR Code para o cliente (2º fator de autenticação,
simulando a necessidade de um celular).
- [x] 9. Cliente - O cliente lê o QR Code e digita o código obtido na tela para enviar para o servidor
- [x] 10. Servidor – Valida o código obtido como 2º fator de autenticação
- [ ] 11. Cliente e Servidor - usam o código para derivar uma chave simétrica de sessão com o PBKDF2
para cifrar a comunicação simétrica entre ambos. Deve ser usada CRIPTOGRAFIA AUTENTICADA
para cifragem e decifragem (modo GCM ou outro)
- [ ] 12. Cliente e Servidor - A partir desse momento o Cliente e o Servidor ficam em um loop trocando
mensagens cifradas com criptografia autenticada
