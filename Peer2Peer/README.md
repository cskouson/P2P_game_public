# Peer2Peer
Summary: The game runs peer 2 peer and switches host(question giver) each time someone guesses correctly. <br/>

<br/>

## To run with gradle:
gradle p2p -Pnick=clint -Pport=9000 -q --console=plain

<br/>
The game will then ask who to connect to, use the form:
192.168.1.100:9999 192.168.1.101:9090

<br/>
Next the game tells you to enter $ symbol to start the game and then asks if you want to be host.  You should <br/>
simply enter 'yes' or 'no'.  If you entered yes it will tell you to enter 'q' to send a question, which will <br/>
then be sent to all peers listening.  The peers can then try to guess as many times as they want until <br/>
someone enters the correct answer.  When someone enters the correct answer they become host and can send a <br/>
question by entering q.  This can continues as long as desired.  To exit enter 'exit' which will not end the <br/>
game for peers that did not enter exit.