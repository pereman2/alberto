
import React from 'react'
import '../css/Resultado.css';
class Resultado extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
        }
    }




    renderResultado() {
        let autores = this.props.data["autores"];
        console.log(this.props.data);
        let res = "" + this.props.pos + ". " + "(" + this.props.type + ") ";
        for (const [key, value] of Object.entries(this.props.data)) {

            let cv = this.getConvertedValue(key, value);
            if (cv !== undefined && !cv.includes("undefined")) {
                console.log(cv);
                res += cv
            }
        }
        return res.slice(0, res.length - 1);
    }

    getConvertedValue(key, value) {
        let res = undefined;
        if (value == "") return res
        switch (key) {
            case "numero":
                res = "Número: " + value + ", ";
                break;
            case "volumen":
                res = "Volumen: " + value + ", ";
                break;
            case "tipo":
                break;
            case "URL":
                res = "\n" + value + "\n";
                break;
            case "autores":
                res = "";
                value.forEach(element => {
                    res += element["nombre"] + " " + element["apellidos"] + ", ";
                });
                break;
            case "inicio":
                res = "\n Página de inicio: " + value + ",\n";
                break;
            case "fin":
                res = "\n Página final: " + value + ",\n";
                break;
            default:
                res = value + ", ";
                break;
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
