import React, {Component} from 'react';
import './_message.css';
import Alert from 'react-s-alert';
import 'react-s-alert/dist/s-alert-default.css';
import 'react-s-alert/dist/s-alert-css-effects/slide.css';
import SockJS from "sockjs-client";
import {getMessage} from "../../api/communicationApi";
import { withCookies } from 'react-cookie';

let Stomp = require("stompjs/lib/stomp.js").Stomp;
let stompClient = null;
let username = null;

class Messages extends Component{
  constructor(props) {
    super(props);

    this.state = {
      authenticated: this.props.authenticated ? this.props.authenticated : false,
      currentUser: this.props.currentUser ? this.props.currentUser : null,
      messages: [],
    }
  }

  async fetchMessage () {
    const messages = await getMessage();
    await this.setState({messages:messages});
  }

  componentDidMount() {
    if (this.state.currentUser) {
      Alert.success("Wellcome " + this.state.currentUser.name + " to Chat Room!")
    }

    let sefl = this;
    var usernameForm = document.querySelector('#usernameForm');
    var messageForm = document.querySelector('#messageForm');
    var connectingElement = document.querySelector('.connecting');
    var usernamePage = document.querySelector('#username-page');
    var chatPage = document.querySelector('#chat-page');
    var messageArea = document.querySelector('#messageArea');
    var messageInput = document.querySelector('#message');

    function connect(event) {
      username = document.querySelector('#name').value.trim();
      // username = this.state.currentUser.email.toString().trim();
      console.log("username: "+ username);
      if(username && username.length) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({},
            function onConnected() {
              // Subscribe to the Public Topic
              stompClient.subscribe('/topic/public', onMessageReceived);
              renderMessageHistory().then(r => console.log(r));
              // Tell your username to the server
              stompClient.send("/app/chat.addUser", {}, JSON.stringify({sender: username, type: 'JOIN'}));
              connectingElement.classList.add('hidden');
            },
            function onError() {
              connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
              connectingElement.style.color = 'red';
            }
        );
      }
      event.preventDefault();
    }

    function sendMessage(event){
      var messageContent = messageInput.value.trim();
      if(messageContent && stompClient) {
        var chatMessage = {
          sender: username,
          content: messageInput.value,
          type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
      }
      event.preventDefault();
    }

    function onMessageReceived(payload) {
      var message = JSON.parse(payload.body);

      var messageElement = document.createElement('li');
      messageElement.className = 'li';

      if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
      } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
      } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
      }
      var textElement = document.createElement('p');
      var messageText = document.createTextNode(message.content);
      textElement.appendChild(messageText);
      messageElement.appendChild(textElement);

      messageArea.appendChild(messageElement);
      messageArea.scrollTop = messageArea.scrollHeight;
    }

    async function renderMessageHistory() {
      await sefl.fetchMessage();
      const { messages } = sefl.state;
      messages.map(item => {
        var messageElement = document.createElement('li');
        messageElement.className = 'li';
        if(item.messageType === 'JOIN') {
          messageElement.classList.add('event-message');
          item.content = item.sender + ' joined!';
        } else if (item.messageType === 'LEAVE') {
          messageElement.classList.add('event-message');
          item.content = item.sender + ' left!';
        } else {
          messageElement.classList.add('chat-message');

          var avatarElement = document.createElement('i');
          var avatarText = document.createTextNode(item.sender[0]);
          avatarElement.appendChild(avatarText);
          avatarElement.style['background-color'] = getAvatarColor(item.sender);

          messageElement.appendChild(avatarElement);

          var usernameElement = document.createElement('span');
          var usernameText = document.createTextNode(item.sender);
          usernameElement.appendChild(usernameText);
          messageElement.appendChild(usernameElement);
        }
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(item.content);
        textElement.appendChild(messageText);
        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
        messageArea.scrollTop = messageArea.scrollHeight;
      })
    }

    function getAvatarColor(messageSender) {
      var colors = ['#2196F3', '#32c787', '#00BCD4', '#ff5652', '#ffc107', '#ff85af', '#FF9800', '#39bbb0'];
      var hash = 0;
      for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
      }
      var index = Math.abs(hash % colors.length);
      return colors[index];
    }

    usernameForm.addEventListener('submit', connect, true);
    messageForm.addEventListener('submit', sendMessage, true)
  }

  render() {
    const { name, email } = this.state.currentUser;
    return (
        <React.Fragment>
          <div id="username-page">
            <div className={"username-page-container"}>
              <h1 className="title h1-mess">Wellcome {name} to chat room!</h1>
              <form id="usernameForm" name="usernameForm">
                <div className="form-group-message">
                  <input type="text" id="name" value={email || ''} className="form-control-message input-mess" readOnly/>
                </div>
                <div className="form-group-message">
                  <button type="submit" className="button-mess accent username-submit">Start Chatting</button>
                </div>
              </form>
            </div>
          </div>
          <div id="chat-page" className={"hidden"}>
            <div className={"chat-container"}>
              <div className="chat-header">
                <h2>Chat Room</h2>
              </div>
              <div className={"connecting"}>
                Connecting...
              </div>
              <ul id="messageArea">

              </ul>
              <form id="messageForm" name="messageForm" nameform="messageForm">
                <div className="form-group-message">
                  <div className="input-group clearfix">
                    <input type="text" id="message" placeholder="Type a message..." autoComplete="off"
                           className="form-control-message input-mess"/>
                    <button type="submit" className="button-mess primary">Send</button>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </React.Fragment>
    );
  }
}
export default withCookies(Messages);