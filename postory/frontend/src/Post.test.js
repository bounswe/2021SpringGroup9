import {screen } from '@testing-library/react';
import { render, unmountComponentAtNode } from "react-dom";
import Post from './Post';
import {MemoryRouter} from "react-router-dom";




let container = null;


beforeEach(() => {
    // setup a DOM element as a render target
    container = document.createElement("div");
    document.body.appendChild(container);
  });
  
afterEach(() => {
    // cleanup on exiting
    unmountComponentAtNode(container);
    container.remove();
    container = null;
  });

test('renders the elements within a dummy post', () => {
    let dummyPost = {title: "test", story : "Praesent eu libero et diam mollis placerat sed eget eros. Curabitur commodo purus in lorem fermentum, a suscipit tellus faucibus. Morbi justo nibh, iaculis sed porttitor id, faucibus in lorem. Aenean porttitor imperdiet velit id laoreet. Mauris tortor urna, fermentum eu eros vel, vestibulum malesuada mi. Vivamus venenatis magna nec eleifend hendrerit. Morbi lacinia ligula a quam varius, ac pretium libero semper. Fusce ultrices arcu ut augue sodales vehicula. Fusce pellentesque urna vel arcu facilisis, sed consectetur enim mollis. Nam suscipit euismod elit, ac cursus ex tempor eget. Curabitur aliquet ante orci, at vestibulum leo finibus vel. Praesent ullamcorper pharetra rhoncus. Vestibulum euismod nulla in ligula bibendum aliquam. Curabitur nec varius ligula. Duis feugiat mi risus, eget auctor lacus scelerisque sit amet.",   
 locations: [{name:"The World"}, {name:"Ankara"}], tags: ["Cool"] };
  render(<MemoryRouter><Post {...dummyPost}/></MemoryRouter>, container);
  expect("Non existing text").toBeInTheDocument;
  for(let element in dummyPost){
    //story is shortened
    if(element == 'story')
        continue;
    // If it is locations or tags
    if(Array.isArray(dummyPost[element]))
        for(let e in dummyPost[element])
            if(element == 'locations')
                expect(container).toContainHTML(dummyPost[element][e]['name']);
            else //tag
                expect(container).toContainHTML(dummyPost[element][e]);
    else
        expect(container).toContainHTML(dummyPost[element]);
  }
});


