import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import CreatePost from './CreatePost';
import EditPost from './EditPost';
import reportWebVitals from './reportWebVitals';
import TopBar from './TopBar';
import SignIn from "./SignIn";
import SignUp from "./SignUp";

import {
  BrowserRouter,
  Routes,
  Route
} from "react-router-dom";
import ForgotPassword from "./ForgotPassword";
import ForgotPasswordConfirm from "./ForgotPasswordConfirm";
import Activation from "./Activation";

ReactDOM.render(
  <BrowserRouter>
    
    <Routes>
    <Route path="/" element={<div><TopBar/> <App /></div>} />
    <Route path="/createPost" element={<div><TopBar/> <CreatePost /></div>} />
    <Route path="/editPost" element={<div><TopBar/> <EditPost /></div>} />
    <Route path="/signIn" element={<div><TopBar /><SignIn /></div>} />
    <Route path="/signUp" element={<div><TopBar /><SignUp /></div>} />
    <Route path="/forgotPassword" element={<div><TopBar /><ForgotPassword /></div>} />
    <Route path="/password/reset/confirm/:uid/:token" element={<div><TopBar /><ForgotPasswordConfirm /></div>} />
    <Route path="/activate/:uid/:token" element={<div><TopBar /><Activation /></div>} />
    </Routes>
  </BrowserRouter>,
  document.getElementById('root')
);

/*
ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);
*/
// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
