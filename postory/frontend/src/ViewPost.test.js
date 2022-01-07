import {fireEvent, screen, waitFor } from '@testing-library/react';
import { render, unmountComponentAtNode } from "react-dom";
import ViewPost from './ViewPost';
import {MemoryRouter, Routes, Route} from "react-router-dom";




let container = null;


beforeEach(() => {
    // setup a DOM element as a render target
    container = document.createElement("div");
    document.body.appendChild(container);

    jest.spyOn(global, 'fetch').mockResolvedValue({
        json: jest.fn().mockResolvedValue({
            "id": 17,
            "title": "My Beloved Laptop",
            "story": "This is my beloved laptop that I used during my BS studies. I have many memories with it. Is it strange to have memories with a computer? Not if you are a CmpE student. I remember so many nights that I worked hard to catch deadlines. This laptop always supported me. It is still in good shape and I use it. ıts still in very good shape.",
            "owner": 13,
            "username": "berkyilmaz",
            "tags": [
                "laptop",
                "cs",
                "bs",
                "odtu",
                "memories"
            ],
            "locations": [
                [
                    "Home",
                    41.05157618702103,
                    28.986976481974125
                ],
                [
                    "Odtü",
                    39.82125989091151,
                    32.84820009022951
                ]
            ],
            "year": [
                2014,
                2018
            ],
            "month": [
                9,
                6
            ],
            "day": [
                28,
                15
            ],
            "hour": [],
            "minute": [],
            "images": [
                "https://group9-451-project.s3.amazonaws.com/image/JPEG_FILE_NAME_uARP455.jpg"
            ],
            "postDate": "2021-12-14T09:52:40.439000Z",
            "editDate": "2021-12-14T14:00:42.931000Z",
            "viewCount": 0,
            "comments": [[1, 'mehmet', 'guzel post'], [2, 'mahmut', 'kotu post']],
            "likeList": [
                [
                    2,
                    "melihaydogd"
                ]
            ],
            "userPhoto": "",
            "popupState": false
        })
      })
  });
  
afterEach(() => {
    // cleanup on exiting
    unmountComponentAtNode(container);
    container.remove();
    container = null;


    jest.restoreAllMocks();

  });

test('Successfully renders the post title after waiting for the dummy post to fetch', async () => {
    /*
    ID            : TC_F_10
    Title         : View Post Page/Renders a post after fetching it from the api
    Test Priority : High
    Module Name   : Frontend - View Post Page
    Description   : Checks whether the post is fetched and rendered properly
    */
    render(<MemoryRouter> <ViewPost /> </MemoryRouter>, container);
    
    //Wait for the post to be fetched
    await waitFor(() => {},  {timeout:2000});
    let element = screen.getByText('My Beloved Laptop');
    expect(element.innerHTML).toBe('My Beloved Laptop');

  }
);

test('Type and send two comments', async () => {
    /*
    ID            : TC_F_11
    Title         : View Post Page/Commenting
    Test Priority : High
    Module Name   : Frontend - View Post Page
    Description   : Checks whether a comment is created after typing and clicking the submit button.
    */
    render(<MemoryRouter> <ViewPost /> </MemoryRouter>, container);
    
    //Wait for the post to be fetched
    await waitFor(() => {},  {timeout:2000});
    let element = screen.getByRole('textbox');
    let send = screen.getByTitle('Post');
    // Create two comments
    fireEvent.change(element, {target : {value:'Comment1'}});
    fireEvent.click(send);
    fireEvent.change(element, {target : {value:'Comment2'}});
    fireEvent.click(send);
    //Should fail if the comments are not generated;
    let created_comment = screen.getByText('Comment1');
    expect(created_comment.innerHTML).toBe('Comment1');
    created_comment = screen.getByText('Comment2');
    expect(created_comment.innerHTML).toBe('Comment2');
  }
);



