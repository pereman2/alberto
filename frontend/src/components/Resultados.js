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
            page: 1,
            pageMax: 5
        }
    }

    isEmpty(obj) {
        return Object.keys(obj).length === 0;
    }


    renderResultados() {
        let resultados = [];
        // iterar tipos : libros articulos congresos
        console.log(this.props.data);
        if (this.props.data !== undefined && this.props.data.length > 0) {
            for (let i = (this.state.page - 1) * this.state.pageMax; i < this.props.data.length && i < this.state.pageMax * this.state.page; i++) {
                let value = this.props.data[i];
                let autores = value["autores"];
                if ((this.props.searchByAutor && !this.isEmpty(autores[0])) || !this.props.searchByAutor) {
                    let resultado = (
                        <Resultado data={value} key={i} pos={i + 1} type={value["tipo"]}></Resultado>
                    )
                    resultados.push(resultado);
                }
            }

        }
        if (resultados.length == 0) {
            return (<div className="res-no-results">No se han encontrado resultados</div>);
        } else {
            return resultados;
        }

    }

    renderPageNumbers() {
        let page = this.state.page;
        const styledPage = (page) => {
            if (page <= 0 || page >= (this.props.data.length / this.state.pageMax + 1)) {
                return "_";
            } else {
                return page;
            }
        }
        return (
            <div className="nav-numbers">
                <span onClick={() => this.movePage(-2)} style={{ fontSize: "30px", fontWeight: "normal", }}>{styledPage(page - 2)}</span>
                <span onClick={() => this.movePage(-1)} style={{ fontSize: "30px", fontWeight: "normal" }}>{styledPage(page - 1)}</span>
                <span style={{ fontSize: "45px", fontWeight: "bold" }}>{page}</span>
                <span onClick={() => this.movePage(+1)} style={{ fontSize: "30px", fontWeight: "normal" }}>{styledPage(page + 1)}</span>
                <span onClick={() => this.movePage(+2)} style={{ fontSize: "30px", fontWeight: "normal" }}>{styledPage(page + 2)}</span>
            </div>
        );
    }

    movePage(movement) {
        let newPage = this.state.page + movement;
        if (newPage > 0 && newPage < this.props.data.length / this.state.pageMax + 1) {
            this.setState({
                page: newPage
            })
        }
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
                    <img className="nav-arrow" onClick={() => this.movePage(-2)} src={leftMoreImage}></img>
                    <img className="nav-arrow" onClick={() => this.movePage(-1)} src={leftImage}></img>
                    <div className="pages-nav">
                        {this.renderPageNumbers()}
                    </div>
                    <img className="nav-arrow" onClick={() => this.movePage(1)} src={rightImage}></img>
                    <img className="nav-arrow" onClick={() => this.movePage(2)} src={rightMoreImage}></img>
                </div>
            </div>
        );
    }
}

export default Resultados;
