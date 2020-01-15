import React, { Component } from 'react';

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
      return <p>Loading...</p>;
    }

    return (
      <div>
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