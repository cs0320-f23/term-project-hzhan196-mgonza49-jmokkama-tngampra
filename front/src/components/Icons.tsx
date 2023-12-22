import { ReactNode } from "react";
import { BrowserRouter, Link } from "react-router-dom";
import "../style/interface.css";

interface IconsProps {
  image: string;
  name: string;
  country: string;
  link: string;
  id: number;
}

function Icons(props: IconsProps): ReactNode {

  

  return (
    <div>
      <Link className="card" key={props.id} to={props.link}>
        <div className="flex justify-center items-center">
          <img src={props.image} alt="country" className="circular-image" />
        </div>

        <div className="card-title">{props.name}</div>

        <div>Location: {props.country}</div>

        {/* <div>
              Terms: {props.term}
            </div> */}
      </Link>
    </div>
  );
}

export default Icons;
