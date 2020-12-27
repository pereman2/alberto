import React from 'react'
import '../css/Resultados.css';
import '../css/App.css';
import Resultado from './Resultado';
import leftImage from '../assets/left.png'
import rightImage from '../assets/right.png'
import leftMoreImage from '../assets/leftMore.png'
import rightMoreImage from '../assets/rightMore.png'

class Resultados extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
        }
    }



    renderResultados() {
        let resultados = [];
        // iterar tipos : libros articulos congresos
        let index = 0;
        for (const [key, value] of Object.entries(this.props.data)) {
            for (let i = 0; i < value.length; i++) {
                let resultado = (
                    <Resultado data={value[i]} key={index} pos={index + 1} type={key}></Resultado>
                )
                resultados.push(resultado);
                index++;
            }
        }
        return resultados;
    }

    renderPageNumbers() {
        let page = this.props.page;
        function styledPage(page) {
            if (page <= 0) {
                return "_"
            } else {
                return page;
            }
        }
        return (
            <div>
                <span onClick={() => this.movePage(-2)} style={{fontSize: "20px", fontWeight:"normal"}}>{styledPage(page - 2)}</span>
                <span onClick={() => this.movePage(-1)} style={{fontSize: "20px", fontWeight:"normal"}}>{styledPage(page - 1)}</span>
                <span style={{fontSize: "40px", fontWeight:"bold"}}>{page}</span>
                <span onClick={() => this.movePage(+1)} style={{fontSize: "20px", fontWeight:"normal"}}>{styledPage(page + 1)}</span>
                <span onClick={() => this.movePage(+2)} style={{fontSize: "20px", fontWeight:"normal"}}>{styledPage(page + 2)}</span>
            </div>
        );
    }

    movePage(movement) {
        this.props.onChangeResultPage(this.props.page + movement);
    }
    render() {
        return (
            <div className="resultados-wrapper">
                <div className="title">Búsqueda bibliográfica de IEI</div>
                <hr className="line"></hr>
                <div className="resultados">
                    {this.renderResultados()}

                </div>
                <div className="page-nav">
                    <img onClick={() => this.movePage(-2)} src={leftMoreImage}></img>
                    <img onClick={() => this.movePage(-1)} src={leftImage}></img>
                    <div className="pages-nav">
                        {this.renderPageNumbers()}
                    </div>
                    <img onClick={() => this.movePage(1)}  src={rightImage}></img>
                    <img onClick={() => this.movePage(2)}  src={rightMoreImage}></img>
                </div>
            </div>
        );
    }
}

export default Resultados;
