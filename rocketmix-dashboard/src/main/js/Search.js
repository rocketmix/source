import React, { Component } from 'react';
import { Spinner, Button, Form, FormGroup, Input } from 'reactstrap';
import Result from './Result';

class Search extends Component {


  constructor(props) {
    super(props);

    // Define component state which contains form fields values
    this.state = {
        isLoading: false,
        searchKeyword: ""
    };

    // Create a DOM reference to be able to call a function on it
    this.searchResults = React.createRef();
  }


  /**
   * Grab form input changes to bind value to component state
   */
  handleChange = (name, e) => {
    this.setState({ [name]: e.target.value });
  }



  /**
   * Perform search by calling remote API, get result and change state on result component to trigger refresh
   */
  performSearch() {
    fetch('http://localhost:8080/actuator/apicalls/' + encodeURI(this.state.searchKeyword))
    .then(response => response.json())
    .then(json => this.searchResults.current.setResults(json));
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
      <div>
        <Form>
          <FormGroup>
            <Input type="text" placeholder="Enter keyword to search" onChange={(e) => this.handleChange("searchKeyword", e)}/>
          </FormGroup>
          <Button variant="primary" onClick={(e) => this.performSearch()}>
            Search
          </Button>
        </Form>
        <Result ref={this.searchResults} />
      </div> 
    );
  }

}

export default Search;
