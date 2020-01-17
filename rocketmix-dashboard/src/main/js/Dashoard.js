import React, { Component } from 'react';
import ReactDOM from "react-dom";
import ApiCall from './component/apicall/ApiCall';

class Dashboard extends Component {


  render() {
    return (
      <div>
        <ApiCall />
      </div> 
    );
  }


}

export default Dashboard;