import React, { Component } from 'react';
import { Spinner, Button, Form, FormGroup, Input } from 'reactstrap';

class App extends Component {
  state = {
    isLoading: true,
    apicalls: []
  };

  async componentDidMount() {
    const response = await fetch('http://localhost:8080/actuator/apicalls');
    const body = await response.json();
    this.setState({ apicalls: body, isLoading: false });
  }

  render() {
    const {apicalls, isLoading} = this.state;

    if (isLoading) {
      return (
        <Spinner animation="border" role="status">
          <span className="sr-only">Loading...</span>
        </Spinner>
      );
    }

    return (
      <div>
        <Form>
          <FormGroup controlId="formSearch">
            <Input type="text" placeholder="Enter keyword to search" />
          </FormGroup>
          <Button variant="primary" type="submit">
            Search
          </Button>
        </Form>
		   blablabla
        <header >
          <div >
            <h2>API CALLS</h2>
            {apicalls.map(apicall =>
              <div key={apicall.id}>
                {apicall.requestURL}
              </div>
            )}
          </div>
        </header>
      </div>
    );
  }
}

export default App;