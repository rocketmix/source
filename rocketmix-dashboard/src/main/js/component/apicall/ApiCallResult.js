import React, { Component } from 'react';
import JSONPretty from 'react-json-pretty';
require('react-json-pretty/themes/monikai.css');

class ApiCallResult extends Component {

    state = {
        results: []
    };


    setResults(newResults) {
        this.setState({results : newResults})
    }


    render() {
        if (this.state.results.length == 0) {
            return (
                <div className="alert alert-light" role="alert">No result</div>
            );
        }
        return (
            <div >
                <JSONPretty id="json-pretty" data={this.state.results}></JSONPretty>
            </div>
        );
    }


}

export default ApiCallResult;