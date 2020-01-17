import React, { Component } from 'react';
import ApiCallSearch from './ApiCallSearch';
import ApiCallResult from './ApiCallResult';

/**
 * Dashboard component that performs full text search on API requests/responses
 */
class ApiCall extends Component {

    constructor(props) {
        super(props);

        // Create DOM references to be able to call a function on it
        this.apiCallSearchComponent = React.createRef();
        this.apiCallResultComponent = React.createRef();

        // Get config
        this.host = document.getElementById('dashboard').dataset.host;
        console.log(this.host);
    }


    /**
     * Perform search by calling remote API, get result and change state on result component to trigger refresh
     */
    performSearch(searchKeyword) {
        if (searchKeyword == "") {
            this.apiCallResultComponent.current.setResults([]);
            return;
        }
        fetch(this.host + '/actuator/apicalls/' + encodeURI(searchKeyword))
            .then(response => response.json())
            .then(json => this.apiCallResultComponent.current.setResults(json));
    }



    /**
     * Note that we set a property function reference to be able to call performSearch from child component
     */
    render() {
        return (
            <div className="m-3">
                <ApiCallSearch ref={this.apiCallSearchComponent} performSearch={(searchKeyword) => this.performSearch(searchKeyword)}/>
                <ApiCallResult ref={this.apiCallResultComponent} />
            </div>
        );
    }


}

export default ApiCall;