import { ReactNode } from "react";
import { BrowserRouter, Link} from 'react-router-dom';


interface IconsProps{
  image: string;
  name: string;
  link: string;
  id: number;
}

function Icons({ image, name, link, id}: IconsProps): ReactNode {
  return (
    <div>

    <Link className="card"
      key={id}
      to={link}>
          <div className="flex justify-center items-center">
            <img src={image} alt="country" className="circular-image" />
          </div>
            {name}
        </Link>

    </div>

   
  );
}

export default Icons;
