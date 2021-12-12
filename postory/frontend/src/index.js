import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import CreatePost from './CreatePost';
import EditPost from './EditPost';
import reportWebVitals from './reportWebVitals';
import TopBar from './TopBar';
import DiscoverPage from './DiscoverPage';
import SignIn from "./SignIn";
import SignUp from "./SignUp";
import ViewPost from './ViewPost';

import {
  BrowserRouter,
  Routes,
  Route
} from "react-router-dom";
import ForgotPassword from "./ForgotPassword";
import ForgotPasswordConfirm from "./ForgotPasswordConfirm";
import Activation from "./Activation";
import { ProfilePageUpper } from './ProfilePage';
import Redirector from "./Redirector";

ReactDOM.render(
  <BrowserRouter>
    <Routes>
    <Route path="/" element={<div><Redirector/><TopBar/> <App /></div>} />
    <Route path="/createPost" element={<div><Redirector/><TopBar/> <CreatePost /></div>} />
    <Route path="/editPost" element={<div><Redirector/><TopBar/> <EditPost /></div>} />
    <Route path="/discover" element={<div><Redirector/><TopBar/> <DiscoverPage /></div>} />
    <Route path="/signIn" element={<div><TopBar /><SignIn /></div>} />
    <Route path="/signUp" element={<div><TopBar /><SignUp /></div>} />
    <Route path="/forgotPassword" element={<div><TopBar /><ForgotPassword /></div>} />
    <Route path="/password/reset/confirm/:uid/:token" element={<div><TopBar /><ForgotPasswordConfirm /></div>} />
    <Route path="/activate/:uid/:token" element={<div><TopBar /><Activation /></div>} />
    <Route path="/viewPost" element={<div><TopBar/> <ViewPost /></div>} />
    <Route path="/profilePage" element={<div><TopBar/> <ProfilePageUpper /></div>} />
    <Route path="/viewPost" element={<div><Redirector/><TopBar/> <ViewPost /></div>} />
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
