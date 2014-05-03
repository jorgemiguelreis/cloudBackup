#Cloud Backup App
## Sistemas Distribuídos 2013/14 - Assignment #2

### Purpose of the Application

A aplicação tem como objetivo principal o backup automático das mensagens sms de um smartphone com tecnologia Android, bem como o backup das informações relacionadas com os registos de chamadas (data, duração, tipo, remetente/emissor,custo) diretamente para um serviço cloud, permitindo ao utilizador controlar melhor os seus gastos e armazenar e sincronizar todas estas informações mesmo que troque de telemóvel ou o mesmo se extravie.
Assim, o utilizador deverá efetuar um registo no serviço, assim como associar uma ou mais contas de serviços cloud onde os backups serão armazenados. Assim, ao realizar login na aplicação, a autenticação no(s) serviço(s) cloud é automática. As pastas com estes conteúdos podem ser partilhadas com outros utilizadores, recebendo cada utilizador notificações quando existirem alterações nas mesmas.


### Main Features
- Recolha automática periódica de registos de mensagens e chamadas de voz;
- Criação de ficheiros XML com a informação das mensagens e chamadas de voz;
- Envio seguro dos ficheiros (encriptados) para o espaço de armazenamento pessoal no serviço cloud GDrive;
- Visualização dos registos armazenados na cloud, em formato userfriendly;
- Partilha de pastas de backup com outros utilizadores registados;
- Recepção de notificações de alteração de conteúdos partilhados.


### Web Services

#### Google Drive Android API:
  - Autenticação e Identificação: Google Account;
  - Serviços: upload e download de ficheiros para serviço cloud GDrive.

#### Servidor de Aplicação:
  - Registo e Autenticação;
  - Base de dados da aplicação:
    - Armazenamento de dados dos utilizadores e respectiva informação de backups;
    - Armazenamento de informação relativa a pastas partilhadas;
  - Envio de notificações (assíncronas) de alterações em pastas partilhadas. Caso o dispositivo não se encontre disponível, a notificação é enviada após login.


### Target Plataforms
  A aplicação destina-se a dispositivos móveis Android (com possibilidade de expansão para Windows Mobile, numa perspetiva futura).


### Additional Services and Improvements (if time permits)

  Como melhoramento, pensou-se em implementar um suporte para outras alternativas de cloud (MeoCloud, Dropbox, OneDrive…), não obrigando assim o utilizador a ter uma conta em apenas um serviço. Neste caso seriam utilizadas também as respetivas API’s destes serviços para as mesmas funcionalidades propostas para o GDrive.
Baseado neste melhoramento seria possível também implementar replicação dos ficheiros para mais do que um serviço cloud com controlo das versões.

Outro objectivo seria possibilitar o backup de outros ficheiros/pastas presentes no dispositivo móvel, como por exemplo, contactos. No caso de fotografias tiradas a partir da câmera do dispositivo móvel, seria interessante realizar o backup para o espaço de armazenamento disponibilizado pelo serviço Flickr, ficando assim guardadas numa galeria em modo privado. 

Em suma, os possíveis melhoramentos da aplicação passarão por uma transição da maior quantidade de informação possível existente que se encontre armazenada no dispositivo e/ou cartão de memória para serviços cloud gratuitos, assim como métodos de replicação segura de conteúdos.
