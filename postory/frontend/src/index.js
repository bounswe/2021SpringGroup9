import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import CreatePost from './CreatePost';
import reportWebVitals from './reportWebVitals';
import TopBar from './TopBar';

import {
  BrowserRouter,
  Routes,
  Route
} from "react-router-dom";

ReactDOM.render(
  <BrowserRouter>
    
    <Routes>
    <Route path="/" element={<div><TopBar/> <App /></div>} />
    <Route path="/createPost" element={<div><TopBar/> <CreatePost /></div>} />
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
