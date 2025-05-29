// src/components/profile/ProfileNav.tsx
import React from 'react';
import './profile.css';

type Section = { id: string; title: string };

const studentSections: Section[] = [
  { id: 'user-info', title: 'User Info' },
  { id: 'contact-info', title: 'Contact Info' },
  { id: 'parent-family', title: 'Parent & Family' },
  { id: 'nutritional', title: 'Nutritional' },
  { id: 'academic', title: 'Academic' },
  { id: 'history', title: 'School History' },
  { id: 'account-settings', title: 'Account Settings' },
];

const nonStudentSections: Section[] = [
  { id: 'user-info', title: 'User Info' },
  { id: 'contact-doe', title: 'Contact & Department of Education' },
  { id: 'civil-family', title: 'Civil Status & Family' },
  { id: 'dependents', title: 'Dependent Children' },
  { id: 'career', title: 'Career & Appointments' },
  { id: 'education', title: 'Educational Background' },
  { id: 'gov-ids', title: 'Government IDs' },
  { id: 'account-settings', title: 'Account Settings' },
];

interface Props {
  isStudent: boolean;
}

const ProfileNav: React.FC<Props> = ({ isStudent }) => {
  const sections = isStudent ? studentSections : nonStudentSections;
  return (
    <nav className="profile-nav">
      <ul>
        {sections.map(({ id, title }) => (
          <li key={id}>
            <a href={`#${id}`}>{title}</a>
          </li>
        ))}
      </ul>
    </nav>
  );
};

export default ProfileNav;
