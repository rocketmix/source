import React, { Component } from 'react';
import { Spinner, Button, Form, FormGroup, Input } from 'reactstrap';
import ApiCallResult from './ApiCallResult';

class ApiCallSearch extends Component {


    constructor(props) {
        super(props);

        // Define component state which contains form fields values
        this.state = {
            searchKeyword: ""
        };

    }


    /**
     * Grab form input changes to bind value to component state
     */
    handleChange = (name, e) => {
        this.setState({ [name]: e.target.value });
        if (name == "searchKeyword" && e.target.value == "") {
            this.props.performSearch(this.state.searchKeyword)
        }
    }

    /**
     * Perform search on Enter keypressed and reset on Escape keypressed.
     * Note that setState is called with callack feature with need to be a function (aka () => {} ) to avoid scope issues
     */
    handleKeyEvent = (e) => {
        if (e.keyCode === 13) this.props.performSearch(this.state.searchKeyword);
        if (e.keyCode === 27) {
            this.setState({searchKeyword : ""}, () => this.props.performSearch(this.state.searchKeyword));
        };
    }



    render() {

        if (this.state.isLoading) {
            return (
                <Spinner animation="border" role="status">
                    <span>Loading...</span>
                </Spinner>
            );
        }

        return (
            <div className="m-3">
                <div className="input-group mb-3">
                    <Input type="text" value={this.state.searchKeyword} placeholder="Enter keyword to search" onKeyDown={(e) => this.handleKeyEvent(e)} onChange={(e) => this.handleChange("searchKeyword", e)} />
                    <div className="input-group-append">
                        <Button variant="primary" onClick={(e) => this.props.performSearch(this.state.searchKeyword)}>Search</Button>
                    </div>
                </div>
            </div>
        );
    }

}

export default ApiCallSearch;
