// src/components/profile/SectionCard.tsx
import React, { ReactNode } from 'react';

interface Props {
  title: string;
  children: ReactNode;
  id?: string;
}

const SectionCard: React.FC<Props> = ({ title, children, id }) => (
  <section id={id} className="card">
    <header>{title}</header>
    <div className="content">{children}</div>
  </section>
);

export default SectionCard;
