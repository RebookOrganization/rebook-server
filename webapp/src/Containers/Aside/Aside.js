import React, {Component} from 'react'
import {Badge, Progress} from "reactstrap";
import InfiniteScroll from "react-infinite-scroller";

class Aside extends Component {
  constructor(props) {
    super(props);
    this.state = {
      listUserChat: [],
      currentUser: this.props.currentUser,
      items: 20,
      hasMoreItems: true
    }
  }

  showItems() {
    let items = [];
    const styleChat = {
      display: 'flex',
      alignItems: 'center',
      padding: '0'
    };

    const dot = {
      height: '8px',
      width: '8px',
      backgroundColor: '#4dbd74',
      borderRadius: '50%',
      display: 'inline-block',
      marginLeft: '100px'
    };
    for (let i = 0; i < this.state.items; i++) {
      items.push(
          <div style={styleChat} key={i}>
            <a className="btn-user">
              <img
                  src={'assets/img/avatars/4.jpg'}
                  className="rounded-circle icon-user"
                  alt="Username"/>
            </a>{' '}
            <p style={{
              fontSize: '15px',
              marginTop: '15px'
            }}>user chat {i}</p>
            <span className={"pull-right"} style={dot}/>
          </div>
      );
    }
    return items;
  };

  loadMore() {
    if (this.state.items === 100) {
      this.setState({ hasMoreItems: false});
    }
    else {
      setTimeout(() => {
        this.setState({ items: this.state.items + 20});
      }, 2000);
    }
  };


  render() {
    return (
        <React.Fragment>
          <div style={{
            position: 'fixed',
            padding: '10px 0 10px 10px',
            height: '100%',
            borderLeft: '1px solid #bbc0c4',
            top: '54px',
            zIndex: '1',
            width: '257px'
          }}
          >
            <h6 style={{color:'#616770'}}>Trò chơi</h6>
            <div className="text-uppercase mb-1 mt-2">
              {/*<small><b>Memory Usage</b></small>*/}
            </div>
            <Progress className="progress-xs" color="warning" value="60"/>
            <small className="text-muted">11444GB/16384MB</small>

            <hr/>
            <h6 style={{color:'#616770'}}>Bắt đầu trò chuyện</h6>
            <div style={{height:'100%', overflow:'auto'}}>
              <InfiniteScroll
                  loadMore={this.loadMore.bind(this)}
                  hasMore={this.state.hasMoreItems}
                  loader={<div className="loader" key={""}><strong> Loading... </strong></div>}
                  useWindow={false}>
                {this.showItems()}{" "}
              </InfiniteScroll>
            </div>

          </div>
          <div className="search" style={{
            position: 'fixed',
            bottom: '0',
            marginLeft: '0',
            zIndex:'1'
          }}>
            <span className="fa fa-search" style={{color:'#bbc0c4', left:'10px', top:'18px'}}/>
            <input style={{marginLeft:'1px', borderRadius: '0', width:'262px', border:'none', height:'45px'}} placeholder={"Tìm kiếm"}/>
          </div>
        </React.Fragment>
    );
  }

}

export default Aside;