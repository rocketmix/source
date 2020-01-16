import React, { Component } from 'react';

class Result extends Component {

    state = {
        results: []
    };


    setResults(newResults) {
        this.setState({results : newResults})
    }


    render() {
        if (this.state.results.length == 0) {
            return (
                <div>No result</div>
            );
        }
        return (
            <div >
                {this.state.results.map(result =>
                    <div key={result.id}>
                        {result.requestURL}
                    </div>
                )}
            </div>
        );
    }


}

export default Result;