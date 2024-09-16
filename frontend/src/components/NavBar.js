import { Link } from "react-router-dom";
import { useUser } from '../auth/AuthProvider.js';
import userAvatar from "../icon/blank-user-avatar.png";

export function NavBar() {
  const user = useUser()

  return (
    <nav className='nav-bar'>
      <div className="logo">DicomDisk</div>

      <div className='nav-bar__item-list'>
          <Link className='nav-bar__item' to={"/storage/search/" + btoa(user.domain)}>
            Диск
          </Link>
          
          <Link className='nav-bar__item' id='profile-link' to={"/profile"}>
              Профиль
              <img style={{ width: "30px", height: "auto"}} key={'user-avatar'} src={userAvatar} />
          </Link>
      </div>
    </nav>
  )            
}
