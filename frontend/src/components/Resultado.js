
import React from 'react'
import '../css/Resultado.css';
class Resultado extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
        }
    }

    

    renderResultado() {
        let res = "" + this.props.pos + ". " + "(" + this.props.type + ") ";
        for(const [key, value] of Object.entries(this.props.data)) {
            res += value + ", "
        }
        return res;
    }

    render() {
        return (
            <div className="resultado">
                {this.renderResultado()}
            </div>
        );
    }
}

export default Resultado;
